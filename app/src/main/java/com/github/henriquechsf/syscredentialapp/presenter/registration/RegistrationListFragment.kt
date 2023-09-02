package com.github.henriquechsf.syscredentialapp.presenter.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.Registration
import com.github.henriquechsf.syscredentialapp.databinding.FragmentRegistrationListBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.presenter.code_scanner.CaptureAct
import com.github.henriquechsf.syscredentialapp.util.CsvGenerator
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.toast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegistrationListFragment : BaseFragment<FragmentRegistrationListBinding>() {

    private val args: RegistrationListFragmentArgs by navArgs()
    private lateinit var event: Event

    private val viewModel: RegistrationListViewModel by viewModels()

    private val registrationAdapter by lazy { RegistrationAdapter() }
    private val csvGenerator by lazy { CsvGenerator(requireActivity(), event) }

    private var registrationList = listOf<Registration>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRegistrationListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        event = args.event
        binding.tvEventTitle.text = event.title

        getRegistrationList()
        initListeners()
        setupRecyclerView()
        observerScanResult()
        observerCountRegistrations()
        configSearchView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_registration, menu)

        val item = menu.findItem(R.id.menu_search)
        binding.searchView.setMenuItem(item)

        super.onCreateOptionsMenu(menu, inflater)
    }

    // TODO: refactor implementation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_report -> {
                val csvHeader = "Nome,Departamento,CheckIn,Evento"
                val csvIntent = csvGenerator.createCsvDataIntent(
                    registrations = registrationAdapter.registrations,
                    header = csvHeader,
                    filename = "relatorio-credenciamento.csv"
                )
                startActivity(csvIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observerCountRegistrations() = lifecycleScope.launch {
        viewModel.countRegistrations.collect {
            binding.tvCountRegistrations.text = it.toString()
        }
    }

    private fun observerScanResult() = lifecycleScope.launch {
        viewModel.scanResult.collect { result ->
            when (result) {
                is ResultState.Success -> {
                    toast(getString(R.string.success_scan, result.data))
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(R.color.green_300))
                }
                is ResultState.Error -> {
                    toast(result.message.toString())
                    binding.cardCountRegistrations
                        .setBackgroundColor(resources.getColor(R.color.red_500))
                }
                else -> {}
            }
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvEventRegistrations.apply {
            adapter = registrationAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initListeners() = with(binding) {
        fabQrcodeScan.setOnClickListener {
            scanCode()
        }
    }

    private fun scanCode() {
        val options = ScanOptions().apply {
            setPrompt(getString(R.string.volume_up_flash_scan))
            setBeepEnabled(true)
            setOrientationLocked(true)
            captureActivity = CaptureAct::class.java
        }
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            insertRegistration(result.contents)
        } else {
            toast(getString(R.string.cancelled_scan))
        }
    }

    private fun insertRegistration(credential: String) {
        event.id?.let { eventId ->
            viewModel.saveRegistration(eventId = eventId, credential = credential)
                .observe(viewLifecycleOwner) { stateView ->
                    when (stateView) {
                        is ResultState.Loading -> {
                            binding.progressBar.show()
                        }
                        is ResultState.Success -> {
                            binding.progressBar.hide()
                            getRegistrationList()
                        }
                        is ResultState.Error -> {
                            binding.progressBar.hide()
                            toast(message = stateView.message ?: "")
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun getRegistrationList() {
        event.id?.let { eventId ->
            viewModel.getRegistrationList(eventId).observe(viewLifecycleOwner) { stateView ->
                when (stateView) {
                    is ResultState.Loading -> {
                        binding.progressBar.show()
                    }
                    is ResultState.Success -> {
                        binding.progressBar.hide()

                        stateView.data?.let {
                            binding.rvEventRegistrations.show()

                            registrationList = it
                            registrationAdapter.registrations = registrationList
                        }
                    }
                    is ResultState.Error -> {
                        binding.progressBar.hide()
                        toast(message = stateView.message ?: "")
                    }
                    is ResultState.Empty -> {
                        binding.progressBar.hide()
                        binding.tvEmptyRegistrations.show()
                    }
                }
            }
        }
    }

    private fun configSearchView() = with(binding) {
        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText.isNotEmpty()) {
                    val newList = registrationList.filter { registration ->
                        registration.userName.contains(newText, true)
                    }
                    registrationAdapter.registrations = newList
                    true
                } else {
                    registrationAdapter.registrations = registrationList
                    false
                }
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                registrationAdapter.registrations = registrationList
            }

            override fun onSearchViewClosedAnimation() {}
            override fun onSearchViewShown() {}
            override fun onSearchViewShownAnimation() {}
        })
    }
}
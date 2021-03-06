package org.wordpress.android.viewmodel.pages

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.launch
import org.wordpress.android.R.string
import org.wordpress.android.fluxc.model.page.PageModel
import org.wordpress.android.fluxc.model.page.PageStatus
import org.wordpress.android.ui.pages.PageItem
import org.wordpress.android.ui.pages.PageItem.Action
import org.wordpress.android.ui.pages.PageItem.Divider
import org.wordpress.android.ui.pages.PageItem.DraftPage
import org.wordpress.android.ui.pages.PageItem.Empty
import org.wordpress.android.ui.pages.PageItem.Page
import org.wordpress.android.ui.pages.PageItem.PublishedPage
import org.wordpress.android.ui.pages.PageItem.ScheduledPage
import org.wordpress.android.ui.pages.PageItem.TrashedPage
import org.wordpress.android.viewmodel.ResourceProvider
import java.util.SortedMap
import javax.inject.Inject

class SearchListViewModel
@Inject constructor(private val resourceProvider: ResourceProvider) : ViewModel() {
    private val _searchResult: MutableLiveData<List<PageItem>> = MutableLiveData()
    val searchResult: LiveData<List<PageItem>> = _searchResult

    private var isStarted: Boolean = false
    private lateinit var pagesViewModel: PagesViewModel

    fun start(pagesViewModel: PagesViewModel) {
        this.pagesViewModel = pagesViewModel

        if (!isStarted) {
            isStarted = true

            pagesViewModel.searchPages.observeForever(searchObserver)
        }
    }

    override fun onCleared() {
        pagesViewModel.searchPages.removeObserver(searchObserver)
    }

    private val searchObserver = Observer<SortedMap<PageStatus, List<PageModel>>> { pages ->
        if (pages != null) {
            loadFoundPages(pages)

            pagesViewModel.checkIfNewPageButtonShouldBeVisible()
        } else {
            _searchResult.postValue(listOf(Empty(string.pages_search_suggestion, true)))
        }
    }

    fun onMenuAction(action: Action, pageItem: Page): Boolean {
        return pagesViewModel.onMenuAction(action, pageItem)
    }

    fun onItemTapped(pageItem: Page) {
        pagesViewModel.onItemTapped(pageItem)
    }

    private fun loadFoundPages(pages: SortedMap<PageStatus, List<PageModel>>) = launch {
        if (pages.isNotEmpty()) {
            val pageItems = pages
                    .map { (status, results) ->
                        listOf(Divider(resourceProvider.getString(status.getTitle()))) +
                                results.map { it.toPageItem(pagesViewModel.arePageActionsEnabled) }
                    }
                    .fold(mutableListOf()) { acc: MutableList<PageItem>, list: List<PageItem> ->
                        acc.addAll(list)
                        return@fold acc
                    }
            _searchResult.postValue(pageItems)
        } else {
            _searchResult.postValue(listOf(Empty(string.pages_empty_search_result, true)))
        }
    }

    private fun PageModel.toPageItem(areActionsEnabled: Boolean): PageItem {
        return when (status) {
            PageStatus.PUBLISHED -> PublishedPage(remoteId, title, actionsEnabled = areActionsEnabled)
            PageStatus.DRAFT -> DraftPage(remoteId, title, actionsEnabled = areActionsEnabled)
            PageStatus.TRASHED -> TrashedPage(remoteId, title, actionsEnabled = areActionsEnabled)
            PageStatus.SCHEDULED -> ScheduledPage(remoteId, title, actionsEnabled = areActionsEnabled)
        }
    }
}

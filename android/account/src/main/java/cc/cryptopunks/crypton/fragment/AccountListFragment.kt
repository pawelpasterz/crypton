package cc.cryptopunks.crypton.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import cc.cryptopunks.crypton.account.R
import cc.cryptopunks.crypton.component.AccountPresentationComponent
import cc.cryptopunks.crypton.presenter.AccountListPresenter
import cc.cryptopunks.crypton.view.AccountListView
import kotlinx.coroutines.launch

class AccountListFragment : PresenterFragment<AccountListPresenter.Actor, AccountListPresenter>() {

    override val layoutRes: Int get() = R.layout.account_list

    override val titleId: Int get() = R.string.manage_accounts

    private val component get() = feature as AccountPresentationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        launch { component.navigationService() }
    }

    override fun onCreatePresenter() = component.accountListPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = AccountListView(
        context = activity!!,
        scope = presentation.actorScope,
        fragmentManager = fragmentManager!!,
        accountItemViewModelProvider = component.accountItemViewModelProvider
    )

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.account_management, menu)
    }
}
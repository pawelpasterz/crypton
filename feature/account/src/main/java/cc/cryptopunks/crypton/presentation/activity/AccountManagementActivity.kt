package cc.cryptopunks.crypton.presentation.activity

import android.os.Bundle
import cc.cryptopunks.crypton.account.R
import cc.cryptopunks.crypton.presentation.fragment.NavigationFragment
import cc.cryptopunks.crypton.util.BaseActivity
import cc.cryptopunks.crypton.util.ext.fragment

class AccountManagementActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_management)
        fragment<NavigationFragment>()
    }
}
package cc.cryptopunks.crypton.activity

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

abstract class FeatureActivity : BaseActivity() {

    private val scope = MainScope()

    val key: Any get() = javaClass.name

    override fun onStart() {
        super.onStart()
        setSupportActionBar(toolbar)
        scope.launch { appCore.routeSys.bind(navController) }
    }

    override fun onStop() {
        scope.coroutineContext.cancelChildren()
        super.onStop()
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        scope.launch { featureCore.selectOptionItem(item.itemId) }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}

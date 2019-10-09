package cc.cryptopunks.crypton.component

import cc.cryptopunks.crypton.fragment.MainFragment
import dagger.Component

@Component(
    dependencies = [
        ApplicationComponent::class,
        NavigationComponent::class
    ]
)
interface AppComponent {
    fun inject(target: MainFragment)
}
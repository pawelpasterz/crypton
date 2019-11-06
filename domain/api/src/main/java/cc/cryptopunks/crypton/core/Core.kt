package cc.cryptopunks.crypton.core

import cc.cryptopunks.crypton.net.Net
import cc.cryptopunks.crypton.repo.Repo
import cc.cryptopunks.crypton.service.Service
import cc.cryptopunks.crypton.sys.Sys
import cc.cryptopunks.crypton.util.BroadcastError
import cc.cryptopunks.crypton.util.ExecutorsComponent

interface Core :
    ExecutorsComponent,
    BroadcastError.Component,
    Net.Component,
    Repo,
    Sys {

    val serviceScope: Service.Scope
}
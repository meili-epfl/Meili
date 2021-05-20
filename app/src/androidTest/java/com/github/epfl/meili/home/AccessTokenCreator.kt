package com.github.epfl.meili.home

import com.facebook.AccessToken
import com.facebook.AccessTokenSource

object AccessTokenCreator {
    fun createToken(grantedPermissions: Collection<String?>?): AccessToken {
        return AccessToken(
            "token", "appId", "userId", grantedPermissions,
            ArrayList<String>(), null, AccessTokenSource.WEB_VIEW, null, null, null
        )
    }
}
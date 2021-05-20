package com.facebook

object AccessTokenCreator {
    /**
     * Used for testing the facebook sign in, creates an access token
     */
    fun createToken(grantedPermissions: Collection<String?>?): AccessToken {
        return AccessToken(
            "token", "appId", "userId", grantedPermissions,
            ArrayList<String>(), null, AccessTokenSource.WEB_VIEW, null, null, null
        )
    }
}
package com.setembreiros.artis.domain.model.profile

import android.net.Uri

data class UserProfile(val username: String, val bio: String, val name: String, val link: String, var image: Uri?)

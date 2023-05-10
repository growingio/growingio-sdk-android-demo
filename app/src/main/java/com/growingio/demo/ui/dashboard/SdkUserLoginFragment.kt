/*
 *   Copyright (c) - 2023 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.growingio.demo.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.AttributesBuilder
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentEventFilterBinding
import com.growingio.demo.databinding.FragmentUserLoginBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.util.GrowingIOManager
import com.growingio.demo.util.MarkwonManager
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class SdkUserLoginFragment : PageFragment<FragmentUserLoginBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserLoginBinding {
        return FragmentUserLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_user_login))

        pageBinding.login.setOnClickListener {
            val userId = pageBinding.userId.editText?.text
            if (userId == null || userId.toString().isEmpty()) {
                showMessage(R.string.sdk_user_id_toast)
                return@setOnClickListener
            }

            val userKey = pageBinding.userKey.editText?.text.toString()
            loginWithUserIdAndKey(userId.toString(), userKey)
        }

        pageBinding.userAttrs.setOnClickListener {
            setLoginUserAttributes()
        }

        pageBinding.userClear.setOnClickListener {
            clearLoginUser()
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug userid")
    }

    override fun onDestroy() {
        super.onDestroy()
        clearLoginUser()
    }

    @SourceCode
    private fun loginWithUserIdAndKey(userId: String, userKey: String) {
        GrowingAutotracker.get().setLoginUserId(userId, userKey)
    }

    @SourceCode
    private fun setLoginUserAttributes() {
        val attrsMap = AttributesBuilder().addAttribute("name", "kun")
            .addAttribute("age", "2.5")
            .addAttribute("hobby", listOf("singing", "dancing", "rap", "playing basketball"))
            .build()
        GrowingAutotracker.get().setLoginUserAttributes(attrsMap)
    }

    @SourceCode
    private fun clearLoginUser() {
        GrowingAutotracker.get().cleanLoginUserId()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 11,
                icon = SdkIcon.Api,
                title = "用户登录",
                desc = "设置登录用户信息和IdMapping功能",
                route = PageNav.SdkUserLoginPage.route(),
                fragmentClass = SdkUserLoginFragment::class
            )
        }
    }
}
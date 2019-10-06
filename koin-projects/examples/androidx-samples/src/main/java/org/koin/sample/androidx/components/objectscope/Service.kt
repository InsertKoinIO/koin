package org.koin.sample.androidx.components.objectscope

import androidx.fragment.app.FragmentActivity

class SomeService(val activity: FragmentActivity)
class OtherService(val fragment: FragmentActivity)
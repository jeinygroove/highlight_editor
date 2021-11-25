/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highlightEditor.fork.text

import androidx.compose.ui.input.key.KeyEvent

/**
 * Platform specific behavior for deselecting text selection in TextField and Text based on
 * special keys, such as the "Back" button on Android.
 *
 * Only return true if selection of should be immediately cancelled in response to this [KeyEvent]
 * as a special case such as the "Back" button on Android. This is not intended for events that
 * would naturally cancel selection due to cursor movement, such as pressing an arrow key.
 *
 * @return true if selection should be cancelled based on this KeyEvent
 */
internal expect fun KeyEvent.cancelsTextSelection(): Boolean

/**
 * macOS has a character/emoji palette, which has to be ordered by application. This
 * platform-specific helper implements this action on MacOS and noop on other platforms.
 */
internal expect fun showCharacterPalette()

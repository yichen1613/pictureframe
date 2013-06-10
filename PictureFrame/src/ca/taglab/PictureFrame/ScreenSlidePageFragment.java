/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.taglab.PictureFrame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.UserTable;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class ScreenSlidePageFragment extends Fragment {

    /**
     * The image file path where the user's picture is located.
     */
    private String mImgPath;

    /**
     * The user's email.
     */
    private String mEmail;

    /**
     * The user's name.
     */
    private String mName;

    /**
     * The user's ID.
     */
    private long mId;

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        mId = Long.valueOf(getArguments().getString(UserTable.COL_ID));
        mImgPath = getArguments().getString(UserTable.COL_IMG);
        mEmail = getArguments().getString(UserTable.COL_EMAIL);
        mName = getArguments().getString(UserTable.COL_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        ((TextView) rootView.findViewById(R.id.name)).setText(mName);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        v.getContext().getApplicationContext(),
                        "Clicked",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        return rootView;
    }
}

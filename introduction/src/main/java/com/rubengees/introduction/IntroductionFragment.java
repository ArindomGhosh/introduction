/*
 *   Copyright 2015 Ruben Gees
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.rubengees.introduction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A Fragment which displays a single Slide.
 *
 * @author Ruben Gees
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class IntroductionFragment extends Fragment {

    private static final String BUNDLE_SLIDE = "introduction_slide";
    private Slide slide;
    private View root;

    public static IntroductionFragment newInstance(@NonNull Slide slide) {
        IntroductionFragment fragment = new IntroductionFragment();
        Bundle args = new Bundle();

        args.putParcelable(BUNDLE_SLIDE, slide);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.slide = getArguments().getParcelable(BUNDLE_SLIDE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = initViews(inflater, container);

        getIntroductionActivity().getStyle().applyStyleOnFragmentView(this, root);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.requestApplyInsets(root);
    }

    @NonNull
    private View initViews(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        ViewGroup root =
                (ViewGroup) inflater.inflate(R.layout.introduction_fragment, container, false);
        ViewGroup contentContainer =
                (ViewGroup) root.findViewById(R.id.introduction_fragment_content_container);

        if (slide.getCustomViewBuilder() == null) {
            contentContainer.addView(initDefaultViews(inflater, container));
        } else {
            contentContainer.addView(initCustomViews(inflater, container));
        }

        root.setBackgroundColor(slide.getColor());
        root.setTag(slide.getPosition());

        return root;
    }

    @NonNull
    private View initDefaultViews(@NonNull LayoutInflater inflater,
                                  @NonNull ViewGroup container) {
        ViewGroup root =
                (ViewGroup) inflater.inflate(R.layout.introduction_fragment_default_content,
                        container, false);
        TextView title =
                (TextView) root.findViewById(R.id.introduction_fragment_default_content_title);
        ImageView image =
                (ImageView) root.findViewById(R.id.introduction_fragment_default_content_image);
        ViewGroup descriptionContainer =
                (ViewGroup) root.findViewById(R.id.introduction_fragment_default_content_description_container);
        TextView description;

        if (slide.getTitle() != null) {
            title.setText(slide.getTitle());
            title.setMaxLines(getLineCountForTitle());
            title.setTypeface(IntroductionConfiguration.getInstance().getTitleTypeface());

            if (slide.getTitleSize() != null) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, slide.getTitleSize());
            }
        }

        if (slide.getDescription() == null && slide.getOption() != null) {
            AppCompatCheckBox option =
                    (AppCompatCheckBox) inflater.inflate(R.layout.introduction_fragment_option,
                            descriptionContainer, false);

            option.setText(slide.getOption().getTitle());
            option.setChecked(slide.getOption().isActivated());
            option.setMaxLines(getLineCountForDescription());
            option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    slide.getOption().setActivated(isChecked);
                }
            });

            CompoundButtonCompat.setButtonTintList(option,
                    ContextCompat.getColorStateList(getContext(), android.R.color.white));

            descriptionContainer.addView(option);
            description = option;
        } else {
            description = (TextView) inflater.inflate(R.layout.introduction_fragment_description,
                    descriptionContainer, false);

            if (slide.getDescription() != null) {
                description.setText(slide.getDescription());
            }

            description.setMaxLines(getLineCountForDescription());
            descriptionContainer.addView(description);
        }

        description.setTypeface(IntroductionConfiguration.getInstance().getDescriptionTypeface());

        if (slide.getDescriptionSize() != null) {
            description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, slide.getDescriptionSize());
        }

        if (slide.getImageResource() != null) {
            image.setImageResource(slide.getImageResource());
        }

        IntroductionConfiguration.getInstance().callOnSlideInit(slide.getPosition(), title,
                image, description);

        return root;
    }

    @NonNull
    private View initCustomViews(@NonNull LayoutInflater inflater,
                                 @NonNull ViewGroup container) {
        return slide.getCustomViewBuilder().buildView(inflater, container);
    }

    private int getLineCountForTitle() {
        return getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE ? 2 : 3;
    }

    private int getLineCountForDescription() {
        return getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE ? 2 : 4;
    }

    public IntroductionActivity getIntroductionActivity() {
        return (IntroductionActivity) getActivity();
    }
}

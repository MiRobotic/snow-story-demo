package com.mirobotic.story.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mirobotic.story.R;
import com.mirobotic.story.app.UserDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mirobotic.story.app.Config.LANG_CANTONESE;
import static com.mirobotic.story.app.Config.LANG_ENGLISH;
import static com.mirobotic.story.app.Config.LANG_HOKKIEN;
import static com.mirobotic.story.app.Config.LANG_MANDARIN;

public class TabsFragment extends Fragment {

    private String type;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        type = getArguments().getString("type");
        return inflater.inflate(R.layout.fragment_tabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        String title = type.substring(0, 1).toUpperCase() + type.substring(1);
        tvTitle.setText(title);

        view.findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        Bundle bundleCan = new Bundle();
        bundleCan.putString("type", type);
        bundleCan.putString("lang", LANG_CANTONESE);

        Bundle bundleEng = new Bundle();
        bundleEng.putString("type", type);
        bundleEng.putString("lang", LANG_ENGLISH);


        Bundle bundleHok = new Bundle();
        bundleHok.putString("type", type);
        bundleHok.putString("lang", LANG_HOKKIEN);


        Bundle bundleMan = new Bundle();
        bundleMan.putString("type", type);
        bundleMan.putString("lang", LANG_MANDARIN);

        FilesFragment fragmentMan, fragmentCan, fragmentHok, fragmentEng;

        fragmentCan = new FilesFragment();
        fragmentEng = new FilesFragment();
        fragmentHok = new FilesFragment();
        fragmentMan = new FilesFragment();

        fragmentCan.setArguments(bundleCan);
        fragmentEng.setArguments(bundleEng);
        fragmentHok.setArguments(bundleHok);
        fragmentMan.setArguments(bundleMan);

        UserDataProvider dataProvider = UserDataProvider.getInstance(getContext());

        String titleCan = LANG_CANTONESE.substring(0, 1).toUpperCase() + LANG_CANTONESE.substring(1);
        String titleEng = LANG_ENGLISH.substring(0, 1).toUpperCase() + LANG_ENGLISH.substring(1);
        String titleHok = LANG_HOKKIEN.substring(0, 1).toUpperCase() + LANG_HOKKIEN.substring(1);
        String titleMan = LANG_MANDARIN.substring(0, 1).toUpperCase() + LANG_MANDARIN.substring(1);

        switch (dataProvider.getLanguage()) {
            case LANG_CANTONESE:
                adapter.addFragment(fragmentCan, titleCan);

                adapter.addFragment(fragmentEng, titleEng);
                adapter.addFragment(fragmentHok, titleHok);
                adapter.addFragment(fragmentMan, titleMan);
                break;
            case LANG_ENGLISH:
                adapter.addFragment(fragmentEng, titleEng);

                adapter.addFragment(fragmentCan, titleCan);
                adapter.addFragment(fragmentHok, titleHok);
                adapter.addFragment(fragmentMan, titleMan);

                break;
            case LANG_HOKKIEN:
                adapter.addFragment(fragmentHok, titleHok);

                adapter.addFragment(fragmentCan, titleCan);
                adapter.addFragment(fragmentEng, titleEng);
                adapter.addFragment(fragmentMan, titleMan);

                break;
            case LANG_MANDARIN:
                adapter.addFragment(fragmentMan, titleMan);

                adapter.addFragment(fragmentCan, titleCan);
                adapter.addFragment(fragmentEng, titleEng);
                adapter.addFragment(fragmentHok, titleHok);

                break;
        }


        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(@NonNull FragmentManager manager) {
            super(manager);

        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}

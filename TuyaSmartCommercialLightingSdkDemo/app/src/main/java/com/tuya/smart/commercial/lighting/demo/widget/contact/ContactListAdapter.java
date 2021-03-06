/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Tuya Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.tuya.smart.commercial.lighting.demo.widget.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuya.smart.android.demo.R;

import java.util.Collections;
import java.util.List;


public class ContactListAdapter extends ArrayAdapter<ContactItemInterface> {

    private int resource; // store the resource layout id for 1 row
    private boolean inSearchMode = false;

    private ContactsSectionIndexer indexer = null;

    public ContactListAdapter(Context _context, int _resource, List<ContactItemInterface> _items) {
        super(_context, _resource, _items);
        resource = _resource;

        // need to sort the mItems array first, then pass it to the indexer
        Collections.sort(_items, new ContactItemComparator());


        setIndexer(new ContactsSectionIndexer(_items));

    }

    // get the section textview from row view
    // the section view will only be shown for the first item
    public TextView getSectionTextView(View rowView) {
        TextView sectionTextView = (TextView) rowView.findViewById(R.id.sectionTextView);
        return sectionTextView;
    }

    public void showSectionViewIfFirstItem(View rowView, ContactItemInterface item, int position) {
        TextView sectionTextView = getSectionTextView(rowView);

        // if in search mode then dun show the section header
        if (inSearchMode) {
            sectionTextView.setVisibility(View.GONE);
        } else {
            // if first item then show the header

            if (indexer.isFirstItemInSection(position)) {

                String sectionTitle = indexer.getSectionTitle(item.getItemForIndex());
                sectionTextView.setText(sectionTitle);
                sectionTextView.setVisibility(View.VISIBLE);

            } else
                sectionTextView.setVisibility(View.GONE);
        }
    }

    // do all the data population for the row here
    // subclass overwrite this to draw more mItems
    public void populateDataForRow(View parentView, ContactItemInterface item, int position) {
        // default just draw the item only
        TextView nameView = (TextView) parentView.findViewById(R.id.nameView);
        nameView.setText(item.getItemForIndex());
    }

    // this should be override by subclass if necessary
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup parentView;

        ContactItemInterface item = getItem(position);

        //Log.i("ContactListAdapter", "item: " + item.getItemForIndex());

        if (convertView == null) {
            parentView = new LinearLayout(getContext()); // Assumption: the resource parent id is a linear layout
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, parentView, true);
        } else {
            parentView = (LinearLayout) convertView;
        }

        // for the very first section item, we will draw a section on top
        showSectionViewIfFirstItem(parentView, item, position);

        // set row mItems here
        populateDataForRow(parentView, item, position);

        return parentView;

    }

    public boolean isInSearchMode() {
        return inSearchMode;
    }

    public void setInSearchMode(boolean inSearchMode) {
        this.inSearchMode = inSearchMode;
    }

    public ContactsSectionIndexer getIndexer() {
        return indexer;
    }

    public void setIndexer(ContactsSectionIndexer indexer) {
        this.indexer = indexer;
    }


}

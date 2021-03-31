package com.nyansapo.presentation.ui.home


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.nyansapo.R
import com.nyansapo.databinding.ItemSpinnerBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


class SpinnerAdapter(context: Context?,   items: QuerySnapshot,   listValue: List<String>,val deleteItem:(DocumentReference)->Unit,val editItem:(DocumentReference,DocumentSnapshot)->Unit) : BaseAdapter() {
    private val TAG = "SpinnerAdapter"

    var inflator: LayoutInflater? = LayoutInflater.from(context)
    var listRefences: QuerySnapshot= items
    var listValues: List<String> = listValue


    override fun getCount(): Int {
        return listRefences.size()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: inflator!!.inflate(R.layout.item_spinner, null)

        val binding = ItemSpinnerBinding.bind(view)
        binding.deleteImageview.setOnClickListener { deleteItem(listRefences.documents[position].reference) }
        binding.editImageView.setOnClickListener { editItem(listRefences.documents[position].reference, listRefences.documents[position]) }
        binding.nameTxtView.text = listValues.get(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflator!!.inflate(R.layout.item_spinner_dropdown, null)

        val binding = ItemSpinnerBinding.bind(view)
        binding.deleteImageview.setOnClickListener { deleteItem(listRefences.documents[position].reference) }
        binding.editImageView.setOnClickListener { editItem(listRefences.documents[position].reference, listRefences.documents[position]) }
        binding.nameTxtView.text = listValues.get(position)
        return view
    }


}
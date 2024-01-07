package com.proyek.paba.feasthub.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.proyek.paba.feasthub.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.proyek.paba.feasthub.api.details.DetailCart
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ShoppingCartAdapter (
    private val listItemCart: ArrayList<DetailCart>
): RecyclerView.Adapter<ShoppingCartAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: DetailCart)
        fun btnMinClicked(data: DetailCart)
        fun btnMaxClicked(data: DetailCart)
        fun deleteItem(data: DetailCart)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _imgThumbnailCart: ImageView = itemView.findViewById(R.id.imgThumbnailCart)
        val _tvJudulCart: TextView = itemView.findViewById(R.id.tvJudulCart)
        val _tvTotalItemCart: TextView = itemView.findViewById(R.id.tvTotalItemCart)
        val _tvTotalHargaCart: TextView = itemView.findViewById(R.id.tvTotalHargaCart)
        val _edQtCart: EditText = itemView.findViewById(R.id.edQtCart)
        val _btnPlusCart: TextView = itemView.findViewById(R.id.btnPlusCart)
        val _btnMinCart: TextView = itemView.findViewById(R.id.btnMinCart)
        val _btnDeleteItem: ImageView = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rc_cart, parent, false)

        return this.ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItemCart.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val itemCart = listItemCart[position]

        holder._tvJudulCart.text = itemCart.nama_item

        val harga = itemCart.harga

        val currencyFormat = NumberFormat.getCurrencyInstance(
            Locale("in", "ID")
        )

        val totalItem = "${itemCart.total_cart} x ${currencyFormat.format(harga)}"
        holder._tvTotalItemCart.text = totalItem


        val totalHarga = itemCart.total_cart * harga
        holder._tvTotalHargaCart.text = currencyFormat.format(totalHarga)
        holder._edQtCart.setText(itemCart.total_cart.toString())

        Picasso.get().load(itemCart.thumbnail).into(holder._imgThumbnailCart)


        holder._tvJudulCart.setOnClickListener {
            this.onItemClickCallback.onItemClicked(itemCart)
        }

        holder._btnPlusCart.setOnClickListener {
            this.onItemClickCallback.btnMaxClicked(itemCart)
        }

        holder._btnMinCart.setOnClickListener {
            this.onItemClickCallback.btnMinClicked(itemCart)
        }

        holder._btnDeleteItem.setOnClickListener {
            this.onItemClickCallback.deleteItem(itemCart)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}
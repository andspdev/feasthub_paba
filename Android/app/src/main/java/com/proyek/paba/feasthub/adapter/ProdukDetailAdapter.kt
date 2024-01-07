package com.proyek.paba.feasthub.adapter

import com.proyek.paba.feasthub.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyek.paba.feasthub.api.detail_transaction.ProdukItems
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ProdukDetailAdapter (
    private val listItem: ArrayList<ProdukItems>
): RecyclerView.Adapter<ProdukDetailAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ProdukItems)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnailCart)
        val _tvJudulShop: TextView = itemView.findViewById(R.id.tvJudulCart)
        val _tvTotalItem: TextView = itemView.findViewById(R.id.tvTotalItemCart)
        val _tvTotalHargaShop: TextView = itemView.findViewById(R.id.tvTotalHargaCart)
        val _btnDetailsShop: Button = itemView.findViewById(R.id.btnDetailsShop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rc_detail_transaction, parent, false)

        return this.ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listItem[position]

        val currencyFormat = NumberFormat.getCurrencyInstance(
            Locale("in", "ID")
        )

        holder._tvJudulShop.text = data.nama_item

        val totalItemHarga = "${data.total_beli} x ${currencyFormat.format(data.harga.toInt())}"
        holder._tvTotalItem.text = totalItemHarga
        holder._tvTotalHargaShop.text = currencyFormat.format(data.total_harga.toInt())

        Picasso.get().load(data.thumbnail).into(holder._imgThumbnail)

        holder._btnDetailsShop.setOnClickListener {
            this.onItemClickCallback.onItemClicked(data)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}
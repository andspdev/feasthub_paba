package com.proyek.paba.feasthub.adapter

import com.proyek.paba.feasthub.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyek.paba.feasthub.api.transaction.ItemsArray
import com.squareup.picasso.Picasso

class TransactionAdapter (
    private val listItem: ArrayList<ItemsArray>
): RecyclerView.Adapter<TransactionAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ItemsArray)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _tvTanggalPembelian: TextView = itemView.findViewById(R.id.tvTanggalPembelian)
        val _tvStatusPembayaran: TextView = itemView.findViewById(R.id.tvStatusPembayaran)
        val _imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnailCart)
        val _tvJudulShop: TextView = itemView.findViewById(R.id.tvJudulCart)
        val _tvTotalItem: TextView = itemView.findViewById(R.id.tvTotalItemCart)
        val _tvTotalHargaShop: TextView = itemView.findViewById(R.id.tvTotalHargaCart)
        val _btnDetailsShop: Button = itemView.findViewById(R.id.btnDetailsShop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rc_transaction, parent, false)

        return this.ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listItem[position]

        holder._tvTanggalPembelian.text = data.dibuat_pada
        holder._tvStatusPembayaran.text = data.status
        holder._tvJudulShop.text = data.title

        val totalItem = "${data.total_item} Items"
        holder._tvTotalItem.text = totalItem
        holder._tvTotalHargaShop.text = data.total_harga

        if (data.status == "selesai") {
            holder._tvStatusPembayaran.text = "Paid"
            holder._tvStatusPembayaran.setBackgroundResource(R.drawable.bg_radius_list_paid)
        }
        else {
            holder._tvStatusPembayaran.text = "Pending"
            holder._tvStatusPembayaran.setBackgroundResource(R.drawable.bg_radius_list_pending)
        }

        if (data.thumbnail != "")
            Picasso.get().load(data.thumbnail).into(holder._imgThumbnail)

        holder._btnDetailsShop.setOnClickListener {
            this.onItemClickCallback.onItemClicked(data)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}
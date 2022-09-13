package com.example.githubapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.githubapp.R
import com.example.githubapp.models.Repository
import io.realm.Realm

class DisplayAdapter(
    private val context: Context,
    private var repositoryList: List<Repository>
): RecyclerView.Adapter<DisplayAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = repositoryList[position]
        holder.setData(current, position)
    }

    override fun getItemCount(): Int = repositoryList.size

    fun swap(dataList: List<Repository>){
//        if(dataList.isEmpty())
//            context.
        repositoryList = dataList
        notifyDataSetChanged()
    }

    @SuppressLint("QueryPermissionsNeeded")
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var pos: Int = 0
        private var current: Repository? = null

        private val imgBookmark: ImageView = itemView.findViewById(R.id.img_bookmark)

        init{
            imgBookmark.setOnClickListener {
                bookmarkRepository(current)
            }

            itemView.setOnClickListener {
                current?.let{
                    val url = it.htmlUrl
                    val webpage = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    if(intent.resolveActivity(context.packageManager) != null){
                        context.startActivity(intent)
                    }
                }
            }
        }

        fun setData(current: Repository?, position: Int){
            current?.let{
                itemView.findViewById<TextView>(R.id.txvName).text = current.name
                itemView.findViewById<TextView>(R.id.txvLanguage).text = current.language
                itemView.findViewById<TextView>(R.id.txvForks).text = current.forks.toString()
                itemView.findViewById<TextView>(R.id.txvWatchers).text = current.language
                itemView.findViewById<TextView>(R.id.txvStars).text = current.language

            }

            this.pos = position
            this.current = current
        }

        private fun bookmarkRepository(current: Repository?) {
            current?.let{
                val realm = Realm.getDefaultInstance()
                realm.executeTransactionAsync { realm ->
                    realm.copyToRealmOrUpdate(current)
                }
            }
        }
    }
}

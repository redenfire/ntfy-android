package io.heckel.ntfy.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.heckel.ntfy.R
import io.heckel.ntfy.data.Subscription
import io.heckel.ntfy.data.topicShortUrl


class MainAdapter(private val onClick: (Subscription) -> Unit, private val onLongClick: (Subscription) -> Unit) :
    ListAdapter<Subscription, MainAdapter.SubscriptionViewHolder>(TopicDiffCallback) {
    val selected = mutableSetOf<Long>()

    /* Creates and inflates view and return TopicViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_fragment_item, parent, false)
        return SubscriptionViewHolder(view, selected, onClick, onLongClick)
    }

    /* Gets current topic and uses it to bind view. */
    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val subscription = getItem(position)
        holder.bind(subscription)
    }

    fun toggleSelection(subscriptionId: Long) {
        if (selected.contains(subscriptionId)) {
            selected.remove(subscriptionId)
        } else {
            selected.add(subscriptionId)
        }
    }

    /* ViewHolder for Topic, takes in the inflated view and the onClick behavior. */
    class SubscriptionViewHolder(itemView: View, val selected: Set<Long>, val onClick: (Subscription) -> Unit, val onLongClick: (Subscription) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private var subscription: Subscription? = null
        private val context: Context = itemView.context
        private val nameView: TextView = itemView.findViewById(R.id.main_item_text)
        private val statusView: TextView = itemView.findViewById(R.id.main_item_status)

        fun bind(subscription: Subscription) {
            this.subscription = subscription
            val statusMessage = if (subscription.notifications == 1) {
                context.getString(R.string.main_item_status_text_one, subscription.notifications)
            } else {
                context.getString(R.string.main_item_status_text_not_one, subscription.notifications)
            }
            nameView.text = topicShortUrl(subscription.baseUrl, subscription.topic)
            statusView.text = statusMessage
            itemView.setOnClickListener { onClick(subscription) }
            itemView.setOnLongClickListener { onLongClick(subscription); true }
            if (selected.contains(subscription.id)) {
                itemView.setBackgroundResource(R.color.primarySelectedRowColor);
            }
        }
    }

    object TopicDiffCallback : DiffUtil.ItemCallback<Subscription>() {
        override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem == newItem
        }
    }
}
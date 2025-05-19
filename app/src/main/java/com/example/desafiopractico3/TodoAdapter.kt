import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiopractico3.*
import com.example.desafiopractico3.Todo

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private val todos = mutableListOf<Todo>()
    private var onItemClick: ((Todo) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tituloTextView: TextView = view.findViewById(R.id.tvTitulo)
        private val descripcionTextView: TextView = view.findViewById(R.id.tvDescripcion)
        private val estadoTextView: TextView = view.findViewById(R.id.tvestado)
        private val fechaTextView: TextView = view.findViewById(R.id.tvfecha)
        private val usuarioTextView: TextView = view.findViewById(R.id.tvusuario)

        fun bind(todo: Todo) {
            tituloTextView.text = todo.title
            descripcionTextView.text = todo.description
            estadoTextView.text = if (todo.done) "Completado" else "Pendiente"
            fechaTextView.text = todo.createdAt
            usuarioTextView.text = todo.createdBy

            itemView.setOnClickListener { onItemClick?.invoke(todo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size

    fun updateList(newList: List<Todo>) {
        println("Lista actualizada con ${newList.size} elementos")

        todos.clear()
        todos.addAll(newList)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Todo) -> Unit) {
        onItemClick = listener
    }
}
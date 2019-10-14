package com.myweb.lec10sqlite

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.edit_delete_layout.view.*
import kotlinx.android.synthetic.main.insert_layout.view.*

class MainActivity : AppCompatActivity() {
    var movieList  = arrayListOf<Movie>()
    var dbHandler: DatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler =  DatabaseHelper(this)
        dbHandler?.writableDatabase
        callMovie()
        recycler_view.adapter = MovieAdapter(movieList,applicationContext)
        recycler_view.layoutManager = LinearLayoutManager(applicationContext) as RecyclerView.LayoutManager?
        recycler_view.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        recycler_view.addItemDecoration(DividerItemDecoration(recycler_view.getContext(), DividerItemDecoration.VERTICAL))

        recycler_view.addOnItemTouchListener(object : OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) {
                Toast.makeText(applicationContext,"You click on : "+ movieList[position].id,
                    Toast.LENGTH_SHORT).show()

                editDeleteDialog(position)






            }
        })
    }

    fun callMovie(){
        movieList.clear();

        movieList.addAll(dbHandler!!.getAllMovies())
        //movieList.add(Movie(2, "Lipta",2134))
        recycler_view.adapter?.notifyDataSetChanged()
        Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
    }


    fun addMovieDialog(v: View){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.insert_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
        //show dialog
        val  mAlertDialog = mBuilder.show()

        mDialogView.btnAddMovie.setOnClickListener{
           // var id = mDialogView.inputId.text.toString()
            var title = mDialogView.inputTitle.text.toString()
            var year = mDialogView.inputYear.text.toString().toInt()
            var result = dbHandler?.insertMovie(Movie(id=0,title = title,year = year))

            if(result!! > -1) {
                Toast.makeText(applicationContext, "The student is added successfully", Toast.LENGTH_SHORT).show()
                callMovie()
                mAlertDialog.dismiss()
            }else{
                Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
            }
        }



    }


    fun editDeleteDialog(position:Int){

        val mov = movieList[position]

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_delete_layout, null)
        //AlertDialogBuilder
        mDialogView.inputIdEDT.setText(mov.id.toString())
        mDialogView.inputIdEDT.isEnabled =false
        mDialogView.inputTitleEDT.setText(mov.title)
        mDialogView.inputYearEDT.setText(mov.year.toString())

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        //// Click on Update button
        mDialogView.btnUpdate.setOnClickListener {
            var id = mDialogView.inputIdEDT.text.toString().toInt()
            var title = mDialogView.inputTitleEDT.text.toString()
            var year = mDialogView.inputYearEDT.text.toString().toInt()
            var result = dbHandler?.updateMovie(Movie(id = id, title = title, year = year))

            if (result!! > -1) {
                Toast.makeText(applicationContext, "The student is updated successfully", Toast.LENGTH_SHORT).show()
                callMovie()

            } else {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
            }
            mAlertDialog.dismiss()
        }
        //// Click on Delete button
        mDialogView.btnDelete.setOnClickListener() {
            //// Assignment 10
            val builder = AlertDialog.Builder(this)
            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                val result = dbHandler?.deleteMovie(mov.id)
                if (result!! > -1) {
                    Toast.makeText(applicationContext, "The student is deleted successfully", Toast.LENGTH_LONG).show()
                    callMovie()
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                }

                mAlertDialog.dismiss()
            }

            val negativeButtonClick = { dialog: DialogInterface, which :Int->
                mAlertDialog.dismiss()
            }

            builder.setTitle("Warning")
            builder.setMessage("Do you want to delete the student?")
            builder.setPositiveButton("No", negativeButtonClick)
            builder.setNegativeButton("Yes" , positiveButtonClick )
            builder.show()

        }/// End btnDelete
    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int, view: View)
}
fun RecyclerView.addOnItemTouchListener(onClickListener: OnItemClickListener) {
    this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {

        override fun onChildViewDetachedFromWindow(view: View) {
            view?.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(view: View) {
            view?.setOnClickListener {
                val holder = getChildViewHolder(view)
                onClickListener.onItemClicked(holder.adapterPosition, view)
            }
        }
    })
}

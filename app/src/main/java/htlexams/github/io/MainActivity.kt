package htlexams.github.io

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import htlexams.github.io.databinding.ActivityMainBinding
import kotlin.math.ceil


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var config: Configuration
    private val storage = Firebase.storage
    private val storageRef = storage.reference.child("images")
    private var imageItems = listOf<StorageReference>()


    private val maxDownloadSize: Long = 1024 * 1024 // 1MB

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        config = resources.configuration
        val sw = config.smallestScreenWidthDp
        val imagesPerRow = Math.round((sw / 200).toDouble())

        storageRef.listAll()
            .addOnSuccessListener { result ->
                for (prefix in result.prefixes) {
                    println("  prefix: $prefix")
                    storage.reference.child("images/${prefix.path}").listAll()
                        .addOnSuccessListener { result ->
                            if (result.items.isNotEmpty())
                                imageItems += result.items[0]
                        }
                }

                for (item in result.items) {
                    imageItems += item
                }
            }
            .addOnSuccessListener {

                val imageTable = binding.imageTable
                val nOfRows = ceil((imageItems.size / imagesPerRow).toDouble()).toInt()
                for (row in 1..nOfRows) {

                    val rowLayout = layoutInflater.inflate(R.layout.row_item, null)
                    val rowView = rowLayout.findViewById<TableRow>(R.id.tableRow)
                    imageTable.addView(rowView)

                    for (i in 1..imagesPerRow) {

                        val item = imageItems[(i + (row - 1) * imagesPerRow).toInt()]
                        val imageItem = layoutInflater.inflate(R.layout.image_item, null)
                        val imageView = imageItem.findViewById<ImageView>(R.id.imageView)
                        rowView.addView(imageItem)

                        item.getBytes(maxDownloadSize).addOnSuccessListener { bytes ->
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
    }
}
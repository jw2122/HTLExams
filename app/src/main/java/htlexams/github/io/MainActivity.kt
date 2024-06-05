package htlexams.github.io

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import htlexams.github.io.databinding.ActivityMainBinding

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val storage = Firebase.storage
    val storageRef = storage.reference.child("images")
    val imageURLs = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef.listAll()
            .addOnSuccessListener { result ->
                for (prefix in result.prefixes) {
                    println("  prefix: $prefix")
                    storage.reference.child("images/${prefix.path}").listAll()
                        .addOnSuccessListener { result ->
                            for (item in result.items) {
                                imageURLs.add(item)
                            }
                        }
                }

                for (item in result.items) {
                    println("  item: $item")
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
    }
}
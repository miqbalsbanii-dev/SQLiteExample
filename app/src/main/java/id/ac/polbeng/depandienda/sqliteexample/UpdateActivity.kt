package id.ac.polbeng.depandienda.sqliteexample

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import id.ac.polbeng.depandienda.sqliteexample.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STUDENT = "extra_student"
    }

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var studentDBHelper: StudentDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val studentData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STUDENT, StudentModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STUDENT)
        }

        studentData?.let {
            binding.etNIM.isEnabled = false
            binding.etNIM.setText(it.nim)
            binding.etNama.setText(it.name)
            binding.etUmur.setText(it.age)
        }

        studentDBHelper = StudentDBHelper(this)

        binding.btnUpdate.setOnClickListener {
            val nim = binding.etNIM.text.toString().trim()
            val name = binding.etNama.text.toString().trim()
            val age = binding.etUmur.text.toString().trim()

            if (nim.isEmpty() || name.isEmpty() || age.isEmpty()) {
                Toast.makeText(
                    this,
                    "Silah masukan data nim, nama, dan umur!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val newUpdateStudent = StudentModel(nim = nim, name = name, age = age)
            val updateCount = studentDBHelper.updateStudent(newUpdateStudent)

            if (updateCount > 0) {
                Toast.makeText(
                    this,
                    "Mahasiswa yang terupdate: $updateCount",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Tidak ada data mahasiswa yang diupdate silahkan coba lagi!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnHapus.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus Data")
                .setMessage("Apakah anda yakin?")
                .setPositiveButton("Ya") { _, _ ->
                    val nim = binding.etNIM.text.toString().trim()
                    val deleteCount = studentDBHelper.deleteStudent(nim)
                    if (deleteCount > 0) {
                        Toast.makeText(
                            this,
                            "Mahasiswa yang terhapus: $deleteCount",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Tidak ada data mahasiswa yang dihapus silahkan coba lagi!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onDestroy() {
        studentDBHelper.close()
        super.onDestroy()
    }
}
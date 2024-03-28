package io.github.teccheck.diary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : DiaryBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
                .commit()
        }

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val import = findPreference<PreferenceScreen>(KEY_IMPORT)
            val export = findPreference<PreferenceScreen>(KEY_EXPORT)

            import?.setOnPreferenceClickListener {
                importBackup()
                true
            }

            export?.setOnPreferenceClickListener {
                exportBackup()
                true
            }
        }

        private fun exportBackup() {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.type = "application/zip"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.export_file_name))
            startActivityForResult(intent, REQUEST_EXPORT_BACKUP)
        }

        private fun importBackup() {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/zip", "text/xml"))
            startActivityForResult(intent, REQUEST_IMPORT_BACKUP)
        }

        @Deprecated("Deprecated in Java")
        override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
            Log.d(TAG, "onActivityResult($requestCode, $resultCode, $intent)")
            super.onActivityResult(requestCode, resultCode, intent)

            if (resultCode == Activity.RESULT_OK) {
                intent?.data?.let { uri ->
                    val context = requireContext()
                    val diaryStorage = (requireActivity().application as DiaryApp).getDiaryStorage()

                    when (requestCode) {
                        REQUEST_IMPORT_BACKUP -> {
                            diaryStorage.importBackup(context, uri)
                            Toast.makeText(context, R.string.import_done, Toast.LENGTH_SHORT).show()
                        }

                        REQUEST_EXPORT_BACKUP -> {
                            diaryStorage.exportBackup(context, uri)
                            Toast.makeText(context, R.string.export_done, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = SettingsActivity::class.simpleName

        private const val KEY_IMPORT = "import_diary"
        private const val KEY_EXPORT = "export_diary"

        private const val REQUEST_IMPORT_BACKUP = 20
        private const val REQUEST_EXPORT_BACKUP = 21
    }
}
package com.example.demoottmobile.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.demoottmobile.R
import com.example.demoottmobile.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

// ═══════════════════════════════════════════════════════
// JAVA TƯƠNG ĐƯƠNG
// ═══════════════════════════════════════════════════════
//
// "@AndroidEntryPoint" → Bắt buộc cho mọi Activity/Fragment dùng Hilt
//   Cho phép Hilt inject vào Activity này.
//
// "class MainActivity : AppCompatActivity()"
//   Java: public class MainActivity extends AppCompatActivity { ... }
//
// "private lateinit var binding: ActivityMainBinding"
//   - "lateinit" → khai báo mà chưa khởi tạo ngay, sẽ gán sau trong onCreate().
//   - Khác với "val": có thể gán một lần sau khi khai báo.
//   Java: private ActivityMainBinding binding; // null ban đầu
//
// "private lateinit var navController: NavController"
//   - NavController quản lý việc đi chuyển giữa các Fragment.
//
// "binding = ActivityMainBinding.inflate(layoutInflater)"
//   - ViewBinding: tự sinh code từ XML, khỏi tạo và bind tất cả view.
//   - Java: ActivityMainBinding.inflate(getLayoutInflater())
//
// "navController.addOnDestinationChangedListener { _, destination, _ ->"
//   - Lambda với 3 tham số, "_" có nghĩa là bỏ qua tham số đó (không dùng).
//   Java: navController.addOnDestinationChangedListener((controller, destination, args) -> { ... });
//
// Java tương đương tổng thể:
//
//   @AndroidEntryPoint
//   public class MainActivity extends AppCompatActivity {
//       private ActivityMainBinding binding;
//       private NavController navController;
//
//       @Override
//       protected void onCreate(Bundle savedInstanceState) {
//           super.onCreate(savedInstanceState);
//           binding = ActivityMainBinding.inflate(getLayoutInflater());
//           setContentView(binding.getRoot());
//           setupNavigation();
//       }
//
//       private void setupNavigation() { ... }
//
//       @Override
//       public boolean onSupportNavigateUp() {
//           return navController.navigateUp() || super.onSupportNavigateUp();
//       }
//   }
// ═══════════════════════════════════════════════════════

// "@AndroidEntryPoint" → bắt buộc để Hilt có thể inject vào đây
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // "lateinit" → sẽ được gán trong onCreate(), không cần khởi tạo ngay
    // Java: private ActivityMainBinding binding;
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // "override fun" = @Override public void/Type trong Java
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // = super.onCreate(savedInstanceState);
        // ViewBinding: tự động bind view, thay thế findViewById()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // "binding.root" = "binding.getRoot()" trong Java
        setupNavigation()
    }

    private fun setupNavigation() {
        // Tìm NavHostFragment từ FragmentManager
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // "as NavHostFragment" → ép kiểu, Java: (NavHostFragment) supportFragmentManager...
        navController = navHostFragment.navController

        // Bottom nav only shows for Home and Channel destinations
        binding.bottomNavigation.setupWithNavController(navController)

        // Lambda với 3 tham số: controller, destination, args
        // "_" = bỏ qua tham số này (không cần dùng)
        // Java: (controller, destination, args) -> { ... }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // "when" = switch-case trong Java nhưng mạnh hơn
                R.id.homeFragment, R.id.channelFragment -> {
                    binding.bottomNavigation.visibility = android.view.View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = android.view.View.GONE
                }
            }
        }
    }

    // "override fun onSupportNavigateUp(): Boolean" = @Override public boolean onSupportNavigateUp()
    // Xử lý nút back trên toolbar
    override fun onSupportNavigateUp(): Boolean {
        // "||" = or. Nếu navController không navigate up được, gọi super
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

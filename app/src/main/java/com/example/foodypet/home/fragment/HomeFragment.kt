package com.example.foodypet.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentHomeBinding
import com.example.foodypet.home.adapter.HomePetPagerAdapter
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.model.PetPagerItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var petList: List<PetPagerItem>
    private var currentPetPosition = 0

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPetPosition = position
            setCurrentIndicator(position)
            updateCurrentPetUI(petList[position])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            currentPetPosition = it.getInt(KEY_CURRENT_PET_POSITION, currentPetPosition)
        }

        petList = listOf(
            PetPagerItem(
                name = "랑이",
                img = R.drawable.cat_1,
                mealTime = "13:00",
                mealContent = "흑돼지 치즈볼 1개, 닭오돌뼈 10g",
                medicineText = "2정 / 6정",
                waterText = "1500 / 2000ml",
                snackText = "1회",
                diaryMealText = "2회 / 5회 급여"
            ),
            PetPagerItem(
                name = "콩이",
                img = R.drawable.dog_1,
                mealTime = "09:30",
                mealContent = "연어 사료 80g, 유산균 1포",
                medicineText = "1정 / 3정",
                waterText = "900 / 1500ml",
                snackText = "2회",
                diaryMealText = "1회 / 3회 급여"
            ),
            PetPagerItem(
                name = "보리",
                img = R.drawable.dog_2,
                mealTime = null,
                mealContent = null,
                medicineText = "0정 / 2정",
                waterText = "700 / 1200ml",
                snackText = "1회",
                diaryMealText = "0회 / 2회 급여"
            )
        )

        val isPetRegistered = petList.isNotEmpty()

        updatePetLockUI(isPetRegistered)

        if (isPetRegistered) {
            if (currentPetPosition >= petList.size) {
                currentPetPosition = 0
            }

            updatePetSection(petList)
            binding.homePetViewPager.setCurrentItem(currentPetPosition, false)
            setCurrentIndicator(currentPetPosition)
            updateCurrentPetUI(petList[currentPetPosition])
        } else {
            updatePetSection(emptyList())
            updatePetName(null)
            updateMealRegisteredUI(false)
            clearMealText()
            clearFoodDiary()
        }

        moveMealAllFragment()
        moveRecommendFragment()
        moveNotification()
        moveDiary()
        moveDiaryList()
        popupSnack()
    }

    private fun updatePetLockUI(isPetRegistered: Boolean) {
        if (isPetRegistered) {
            binding.homeLockedOverlayContainer.visibility = View.GONE

            binding.homeQuickMealTitleTv.alpha = 1f
            binding.homeQuickMealSubTitleTv.alpha = 1f
            binding.homeQuickMealRegisteredCv.alpha = 1f
            binding.homeQuickMealEmptyCv.alpha = 1f

            binding.homeFoodDiaryTitleTv.alpha = 1f
            binding.homeFoodDiarySubTitleTv.alpha = 1f
            binding.homeFoodDiaryCv.alpha = 1f
        } else {
            binding.homeQuickMealTitleTv.alpha = 0.55f
            binding.homeQuickMealSubTitleTv.alpha = 0.55f
            binding.homeQuickMealRegisteredCv.alpha = 0.55f
            binding.homeQuickMealEmptyCv.alpha = 0.55f

            binding.homeFoodDiaryTitleTv.alpha = 0.55f
            binding.homeFoodDiarySubTitleTv.alpha = 0.55f
            binding.homeFoodDiaryCv.alpha = 0.55f

            binding.homeLockedOverlayContainer.visibility = View.VISIBLE
        }
    }

    private fun updatePetName(petName: String?) {
        val displayName = if (petName.isNullOrBlank()) "OO" else petName
        binding.homeQuickMealTitleTv.text = displayName
        binding.homeFoodDiaryTitleTv.text = displayName
    }

    private fun updateFoodDiary(
        medicineText: String,
        waterText: String,
        snackText: String,
        mealText: String
    ) {
        binding.homeFoodDiaryMedicineTv.text = medicineText
        binding.homeFoodDiaryWaterTv.text = waterText
        binding.homeFoodDiarySnackTv.text = snackText
        binding.homeFoodDiaryMealTv.text = mealText
    }

    private fun updateMealRegisteredUI(isMealRegistered: Boolean) {
        if (isMealRegistered) {
            binding.homeQuickMealRegisteredCv.visibility = View.VISIBLE
            binding.homeQuickMealEmptyCv.visibility = View.GONE
            binding.homeBtnAllFoodLl.visibility = View.VISIBLE
        } else {
            binding.homeQuickMealRegisteredCv.visibility = View.GONE
            binding.homeQuickMealEmptyCv.visibility = View.VISIBLE
            binding.homeBtnAllFoodLl.visibility = View.GONE
        }
    }

    private fun updatePetSection(petList: List<PetPagerItem>) {
        if (petList.isEmpty()) {
            binding.homePetEmptyCv.visibility = View.VISIBLE
            binding.homePetViewPager.visibility = View.GONE
            binding.homeIndicatorLayout.visibility = View.GONE
            return
        }

        binding.homePetEmptyCv.visibility = View.GONE
        binding.homePetViewPager.visibility = View.VISIBLE
        binding.homeIndicatorLayout.visibility =
            if (petList.size > 1) View.VISIBLE else View.GONE

        val adapter = HomePetPagerAdapter(petList)
        binding.homePetViewPager.adapter = adapter

        setupIndicators(petList.size)

        binding.homePetViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.homePetViewPager.registerOnPageChangeCallback(pageChangeCallback)

        binding.homePetViewPager.setCurrentItem(currentPetPosition, false)
        setCurrentIndicator(currentPetPosition)
    }

    private fun updateCurrentPetUI(pet: PetPagerItem) {
        updatePetName(pet.name)

        updateFoodDiary(
            medicineText = pet.medicineText,
            waterText = pet.waterText,
            snackText = pet.snackText,
            mealText = pet.diaryMealText
        )

        val hasMeal = !pet.mealTime.isNullOrBlank() && !pet.mealContent.isNullOrBlank()
        updateMealRegisteredUI(hasMeal)

        if (hasMeal) {
            binding.homeQuickMealTimeTv.text = pet.mealTime
            binding.homeQuickMealContentTv.text = pet.mealContent
        } else {
            clearMealText()
        }
    }

    private fun clearMealText() {
        binding.homeQuickMealTimeTv.text = ""
        binding.homeQuickMealContentTv.text = ""
    }

    private fun clearFoodDiary() {
        binding.homeFoodDiaryMedicineTv.text = ""
        binding.homeFoodDiaryWaterTv.text = ""
        binding.homeFoodDiarySnackTv.text = ""
        binding.homeFoodDiaryMealTv.text = ""
    }

    private fun setupIndicators(count: Int) {
        binding.homeIndicatorLayout.removeAllViews()

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        repeat(count) {
            val imageView = ImageView(requireContext()).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_unselect
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.homeIndicatorLayout.addView(imageView)
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.homeIndicatorLayout.childCount

        for (i in 0 until childCount) {
            val imageView = binding.homeIndicatorLayout.getChildAt(i) as ImageView
            val drawableId = if (i == position) {
                R.drawable.indicator_select
            } else {
                R.drawable.indicator_unselect
            }

            imageView.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), drawableId)
            )
        }
    }

    private fun moveMealAllFragment() {
        binding.homeBtnAllFoodLl.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MealAllFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun moveRecommendFragment() {
        binding.homeQuickMealEmptyCv.setOnClickListener {
            showDietRegisterDialog()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_PET_POSITION, currentPetPosition)
    }

    override fun onDestroyView() {
        binding.homePetViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val KEY_CURRENT_PET_POSITION = "current_pet_position"
    }

    private fun showDietRegisterDialog() {
        val dialog = DietRegisterDialogFragment()

        dialog.setOnRecommendClickListener {
            if (petList.isEmpty()) return@setOnRecommendClickListener

            val currentPet = petList[currentPetPosition]
            val isMealEmpty =
                currentPet.mealTime.isNullOrBlank() && currentPet.mealContent.isNullOrBlank()

            if (isMealEmpty) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MealRecommendFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        dialog.setOnDirectClickListener {
            if (petList.isEmpty()) return@setOnDirectClickListener

            val currentPet = petList[currentPetPosition]
            val isMealEmpty =
                currentPet.mealTime.isNullOrBlank() && currentPet.mealContent.isNullOrBlank()

            if (isMealEmpty) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MealEditorFragment.newInstance(MealEditorMode.CREATE))
                    .addToBackStack(null)
                    .commit()
            }
        }

        dialog.show(parentFragmentManager, "DietRegisterDialog")
    }

    private fun moveNotification() {
        binding.homeAlarmIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeNotificationFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun moveDiary() {
        binding.homeFoodDiaryRecordCv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DiaryFragment.newInstance(DiaryMode.REGISTER))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun moveDiaryList() {
        binding.homeFoodDiaryMealCv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MealDiaryListFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun popupSnack() {
        binding.homeFoodDiarySnackCv.setOnClickListener {
            val dialog = SnackDialogFragment()
            dialog.show(parentFragmentManager, "SnackDialog")
        }
    }
}
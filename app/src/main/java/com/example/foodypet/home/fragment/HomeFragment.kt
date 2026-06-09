package com.example.foodypet.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.foodypet.R
import com.example.foodypet.databinding.FragmentHomeBinding
import com.example.foodypet.home.adapter.HomePetPagerAdapter
import com.example.foodypet.home.dto.CapsuleIntakeCreateItem
import com.example.foodypet.home.dto.CapsuleIntakeCreateRequest
import com.example.foodypet.home.dto.HomePetTodayResponse
import com.example.foodypet.home.dto.WaterIntakeCreateRequest
import com.example.foodypet.home.enum.DiaryMode
import com.example.foodypet.home.model.NutritionUiModel
import com.example.foodypet.home.model.PetPagerItem
import com.example.foodypet.network.RetrofitClient
import com.example.foodypet.stock.fragment.StockFragment
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels
import com.example.foodypet.community.model.ConnectMealPetItem
import com.example.foodypet.community.viewmodel.CommunityPostSharedViewModel

class HomeFragment : Fragment() {

    private val communityPostSharedViewModel: CommunityPostSharedViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var petList: List<PetPagerItem> = emptyList()
    private var currentPetPosition = 0

    override fun onResume() {
        super.onResume()
        loadTodayDiaries()
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            if (position !in petList.indices) return

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

        moveMealAllFragment()
        moveRecommendFragment()
        moveNotification()
        moveDiary()
        moveDiaryList()
        popupSnack()
        popupWater()
        popupMedicine()
        moveStock()
    }

    private fun loadTodayDiaries() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getTodayDiaries()

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        petList = body.pets.map { pet ->
                            pet.toPetPagerItem()
                        }

                        communityPostSharedViewModel.setPetItems(
                            petList.map { pet ->
                                ConnectMealPetItem(
                                    petId = pet.petId,
                                    petName = pet.name
                                )
                            }
                        )

                        updateHomeWithPetList()
                    } else {
                        showHomeLoadFail()
                    }
                } else {
                    Log.e("HomeFragment", "오늘 기록 조회 실패 code: ${response.code()}")
                    showHomeLoadFail()
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "오늘 기록 조회 오류", e)
                showHomeLoadFail()
            }
        }
    }

    private fun updateHomeWithPetList() {
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
    }

    private fun HomePetTodayResponse.toPetPagerItem(): PetPagerItem {
        return PetPagerItem(
            petId = petId,
            name = petName,
            imgUrl = petImg,
            mealTime = todayMeal.mealTime?.take(5),
            mealContent = todayMeal.mealText.takeIf {
                todayMeal.exists && it.isNotBlank()
            },
            medicineText = "${diary.capsuleDiary.givenCount}회 / ${diary.capsuleDiary.targetCount}회",
            waterText = diary.waterDiary.displayText,
            snackText = diary.treatDiary.displayText,
            diaryMealText = diary.mealDiary.displayText
        )
    }

    private fun showHomeLoadFail() {
        if (!isAdded) return

        Toast.makeText(
            requireContext(),
            "홈 정보를 불러올 수 없습니다.",
            Toast.LENGTH_SHORT
        ).show()

        petList = emptyList()
        communityPostSharedViewModel.clearPetItems()

        updatePetLockUI(false)
        updatePetSection(emptyList())
        updatePetName(null)
        updateMealRegisteredUI(false)
        clearMealText()
        clearFoodDiary()
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

    private fun showDietRegisterDialog() {
        val dialog = DietRegisterDialogFragment()

        dialog.setOnRecommendClickListener {
            if (petList.isEmpty()) return@setOnRecommendClickListener
            if (currentPetPosition !in petList.indices) return@setOnRecommendClickListener

            val currentPet = petList[currentPetPosition]
            val isMealEmpty =
                currentPet.mealTime.isNullOrBlank() && currentPet.mealContent.isNullOrBlank()

            if (isMealEmpty) {
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        MealRecommendFragment.newInstance(currentPet.petId)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }

        dialog.setOnDirectClickListener {
            if (petList.isEmpty()) return@setOnDirectClickListener
            if (currentPetPosition !in petList.indices) return@setOnDirectClickListener

            val currentPet = petList[currentPetPosition]
            val isMealEmpty =
                currentPet.mealTime.isNullOrBlank() && currentPet.mealContent.isNullOrBlank()

            if (isMealEmpty) {
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        MealEditorFragment.newInstance(MealEditorMode.CREATE)
                    )
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
            if (petList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "등록된 반려동물이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (currentPetPosition !in petList.indices) {
                Toast.makeText(
                    requireContext(),
                    "반려동물 정보를 확인할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val currentPet = petList[currentPetPosition]

            Log.d(
                "HomeFragment",
                "밥일기 등록 이동 petId=${currentPet.petId}, petName=${currentPet.name}, mealTime=${currentPet.mealTime}, mealContent=${currentPet.mealContent}"
            )

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    DiaryFragment.newInstance(
                        mode = DiaryMode.REGISTER,
                        petId = currentPet.petId,
                        petName = currentPet.name,
                        mealTime = currentPet.mealTime,
                        mealContent = currentPet.mealContent
                    )
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun moveStock() {
        binding.homeStockIv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StockFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun moveDiaryList() {
        binding.homeFoodDiaryMealCv.setOnClickListener {
            if (petList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "등록된 반려동물이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (currentPetPosition !in petList.indices) {
                Toast.makeText(
                    requireContext(),
                    "반려동물 정보를 확인할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val currentPet = petList[currentPetPosition]

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MealDiaryListFragment.newInstance(
                        petId = currentPet.petId,
                        petName = currentPet.name
                    )
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun popupSnack() {
        binding.homeFoodDiarySnackCv.setOnClickListener {
            if (petList.isEmpty()) {
                Toast.makeText(requireContext(), "반려동물 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentPet = petList[currentPetPosition]
            val petId = currentPet.petId

            val dialog = SnackDialogFragment.newInstance(petId)
            dialog.show(parentFragmentManager, "SnackDialog")
        }
    }

    private fun popupWater() {
        binding.homeFoodDiaryWaterCv.setOnClickListener {
            if (petList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "등록된 반려동물이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (currentPetPosition !in petList.indices) {
                Toast.makeText(
                    requireContext(),
                    "반려동물 정보를 확인할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val currentPet = petList[currentPetPosition]

            loadWaterIntakesAndShowDialog(
                petId = currentPet.petId
            )
        }
    }

    private fun loadWaterIntakesAndShowDialog(petId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getWaterIntakes(petId)

                if (response.isSuccessful) {
                    val body = response.body()

                    val initialValues = body?.waterIntakeItems
                        ?.map { item ->
                            item.amountMl.toInt()
                        }
                        ?: emptyList()

                    showWaterAmountDialog(
                        petId = petId,
                        initialValues = initialValues
                    )

                } else {
                    Log.e(
                        "HomeFragment",
                        "음수량 조회 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    showWaterAmountDialog(
                        petId = petId,
                        initialValues = emptyList()
                    )
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "음수량 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showWaterAmountDialog(
        petId: Long,
        initialValues: List<Int>
    ) {
        var dialog: WaterAmountDialog? = null

        dialog = WaterAmountDialog(
            context = requireContext(),
            initialValues = initialValues,
            onSaveClick = { totalAmount, inputValues ->
                saveWaterIntakes(
                    petId = petId,
                    totalAmount = totalAmount,
                    inputValues = inputValues,
                    dialog = dialog
                )
            }
        )

        dialog.show()
    }

    private fun saveWaterIntakes(
        petId: Long,
        totalAmount: Int,
        inputValues: List<Int>,
        dialog: WaterAmountDialog?
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val request = WaterIntakeCreateRequest(
                    amountsMl = inputValues.map { amount ->
                        amount.toLong()
                    }
                )

                val response = RetrofitClient.apiService.createWaterIntakes(
                    petId = petId,
                    request = request
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    Toast.makeText(
                        requireContext(),
                        body?.message ?: "성공적으로 처리되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.homeFoodDiaryWaterTv.text = "${totalAmount}ml"

                    dialog?.dismiss()

                    loadTodayDiaries()

                } else {
                    Log.e(
                        "HomeFragment",
                        "음수량 기록 저장 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "음수량 기록 저장에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "음수량 기록 저장 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun popupMedicine() {
        binding.homeFoodDiaryMedicineCv.setOnClickListener {
            if (petList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "등록된 반려동물이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (currentPetPosition !in petList.indices) {
                Toast.makeText(
                    requireContext(),
                    "반려동물 정보를 확인할 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val currentPet = petList[currentPetPosition]

            loadCapsuleIntakesAndShowDialog(
                petId = currentPet.petId
            )
        }
    }

    private fun loadCapsuleIntakesAndShowDialog(petId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCapsuleIntakes(petId)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body == null) {
                        Toast.makeText(
                            requireContext(),
                            "영양제 정보를 불러올 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val nutritionList = body.capsules.map { capsule ->
                        NutritionUiModel(
                            nutritionId = capsule.petCapsuleId,
                            nutritionName = capsule.capsuleName,
                            requiredCount = capsule.capsuleCount,
                            takenCount = capsule.givenCount
                        )
                    }.toCollection(ArrayList())

                    if (nutritionList.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "등록된 영양제가 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val dialog = NutritionDialogFragment(
                        nutritionList = nutritionList,
                        onSaveClick = { updatedList ->
                            saveCapsuleIntakes(
                                petId = petId,
                                updatedList = updatedList
                            )
                        }
                    )

                    dialog.show(parentFragmentManager, "MedicineDialog")

                } else {
                    Log.e(
                        "HomeFragment",
                        "영양제 목록 조회 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "영양제 정보를 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "영양제 목록 조회 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveCapsuleIntakes(
        petId: Long,
        updatedList: List<NutritionUiModel>
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val request = CapsuleIntakeCreateRequest(
                    capsuleIntakes = updatedList.map { item ->
                        CapsuleIntakeCreateItem(
                            petCapsuleId = item.nutritionId,
                            givenCount = item.takenCount
                        )
                    }
                )

                val response = RetrofitClient.apiService.createCapsuleIntakes(
                    petId = petId,
                    request = request
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    Toast.makeText(
                        requireContext(),
                        body?.message ?: "성공적으로 처리되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadTodayDiaries()

                } else {
                    Log.e(
                        "HomeFragment",
                        "영양제 기록 저장 실패 code=${response.code()}, error=${response.errorBody()?.string()}"
                    )

                    Toast.makeText(
                        requireContext(),
                        "영양제 기록 저장에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "영양제 기록 저장 오류", e)

                Toast.makeText(
                    requireContext(),
                    "서버 연결 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
}
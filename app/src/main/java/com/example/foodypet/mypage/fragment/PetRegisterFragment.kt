package com.example.foodypet.mypage.fragment

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.foodypet.data.remote.dto.MealInfoDto
import com.example.foodypet.data.remote.dto.MealTimeDto
import com.example.foodypet.data.remote.dto.PetAssignFormDto
import com.example.foodypet.data.remote.dto.PetInfoDto
import com.example.foodypet.data.remote.dto.SupplementDto
import com.example.foodypet.databinding.FragmentPetRegisterBinding
import com.example.foodypet.mypage.adapter.PetRegisterPagerAdapter
import com.example.foodypet.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PetRegisterFragment : Fragment(),
    PetRegisterStep1Fragment.StepMoveListener,
    PetRegisterStep2Fragment.StepMoveListener,
    PetRegisterStep3Fragment.StepMoveListener,
    PetRegisterStep4Fragment.StepMoveListener {

    private var _binding: FragmentPetRegisterBinding? = null
    private val binding get() = _binding!!

    private var mode: String = MODE_REGISTER
    private lateinit var pagerAdapter: PetRegisterPagerAdapter

    private var isSubmitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString(ARG_MODE) ?: MODE_REGISTER
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewPager()
    }

    private fun setupViewPager() {
        pagerAdapter = PetRegisterPagerAdapter(
            fragment = this,
            mode = mode
        )

        binding.petRegisterViewPager.adapter = pagerAdapter
        binding.petRegisterViewPager.isUserInputEnabled = false
        binding.petRegisterViewPager.offscreenPageLimit = 4
    }

    override fun moveToStep(step: Int) {
        if (mode != MODE_EDIT) return
        binding.petRegisterViewPager.setCurrentItem(step, true)
    }

    override fun moveToNextStep() {
        val current = binding.petRegisterViewPager.currentItem

        if (current < 3) {
            binding.petRegisterViewPager.setCurrentItem(current + 1, true)
        }
    }

    override fun moveToPrevStep() {
        val current = binding.petRegisterViewPager.currentItem

        if (current > 0) {
            binding.petRegisterViewPager.setCurrentItem(current - 1, true)
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    override fun submitPetRegister() {
        if (isSubmitting) return

        if (mode == MODE_REGISTER) {
            registerPet()
        } else {
            Toast.makeText(
                requireContext(),
                "수정 API는 아직 연결되지 않았습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun registerPet() {
        val formDto = buildPetAssignFormDto() ?: return
        val petAssignFormDtoPart = buildPetAssignFormDtoPart(formDto)
        val imagePart = buildImagePart() ?: return

        isSubmitting = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.registerPet(
                    petAssignFormDto = petAssignFormDtoPart,
                    image = imagePart
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        response.body()?.message ?: "반려동물 등록이 완료되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "반려동물 등록에 실패했습니다. (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "네트워크 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isSubmitting = false
            }
        }
    }

    private fun buildPetAssignFormDtoPart(
        formDto: PetAssignFormDto
    ): MultipartBody.Part {
        val json = Gson().toJson(formDto)

        val requestBody = json.toRequestBody(
            "application/json; charset=utf-8".toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData(
            "data",
            null,
            requestBody
        )
    }

    private fun buildPetAssignFormDto(): PetAssignFormDto? {
        val step1 = pagerAdapter.step1Fragment
        val step2 = pagerAdapter.step2Fragment
        val step3 = pagerAdapter.step3Fragment
        val step4 = pagerAdapter.step4Fragment

        val name = step1.getPetName().trim()
        val birthDate = step1.getFormattedBirthDate()
        val weightKg = step1.getPetWeightKg()

        if (name.isBlank()) {
            showToast("이름을 입력해주세요.")
            binding.petRegisterViewPager.setCurrentItem(0, true)
            return null
        }

        if (birthDate.isBlank()) {
            showToast("생일을 입력해주세요.")
            binding.petRegisterViewPager.setCurrentItem(0, true)
            return null
        }

        if (weightKg == null || weightKg <= 0.0) {
            showToast("몸무게를 올바르게 입력해주세요.")
            binding.petRegisterViewPager.setCurrentItem(0, true)
            return null
        }

        val petType = mapPetType(step2.getSpecies())
        val breed = step2.getBreed()

        val mealCount = step3.getMealCount()
        val rawMealTimes = step3.getMealTimes()
        val mealTimes = mutableListOf<MealTimeDto>()

        rawMealTimes.forEachIndexed { index, time ->
            val formattedTime = formatMealTime(time)

            if (formattedTime == null) {
                showToast("${index + 1}회 급여 시간을 입력해주세요. 예: 08:00")
                binding.petRegisterViewPager.setCurrentItem(2, true)
                return null
            }

            mealTimes.add(
                MealTimeDto(
                    sequence = index + 1,
                    time = formattedTime
                )
            )
        }

        if (mealTimes.size != mealCount) {
            showToast("급여 시간을 모두 입력해주세요.")
            binding.petRegisterViewPager.setCurrentItem(2, true)
            return null
        }

        val supplements = step4.getSupplements()
            .filter { it.name.isNotBlank() && it.count > 0 }
            .map {
                SupplementDto(
                    name = it.name,
                    countPerDay = it.count
                )
            }

        return PetAssignFormDto(
            petInfo = PetInfoDto(
                name = name,
                birthDate = birthDate,
                weightKg = weightKg,
                petType = petType,
                dogBreed = if (petType == "DOG") mapDogBreed(breed) else null,
                catBreed = if (petType == "CAT") mapCatBreed(breed) else null,
                gender = mapGender(step2.getGender()),
                neuteredStatus = mapNeuteredStatus(step2.getNeutered())
            ),
            mealInfo = MealInfoDto(
                mealCount = mealCount,
                mealTimes = mealTimes
            ),
            supplements = supplements
        )
    }

    private fun buildImagePart(): MultipartBody.Part? {
        val imageUri = pagerAdapter.step1Fragment.getSelectedImageUri()

        if (imageUri == null) {
            showToast("반려동물 사진을 선택해주세요.")
            binding.petRegisterViewPager.setCurrentItem(0, true)
            return null
        }

        val file = uriToFile(imageUri)

        val mimeType = requireContext().contentResolver.getType(imageUri)
            ?: "image/jpeg"

        val requestBody = file.asRequestBody(
            mimeType.toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData(
            name = "image",
            filename = file.name,
            body = requestBody
        )
    }

    private fun uriToFile(uri: Uri): File {
        val fileName = getFileName(uri)
        val file = File(requireContext().cacheDir, fileName)

        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "pet_image_${System.currentTimeMillis()}.jpg"

        val cursor = requireContext().contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (it.moveToFirst() && nameIndex >= 0) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName
    }

    private fun mapPetType(species: String): String {
        return when (species) {
            "강아지" -> "DOG"
            "고양이" -> "CAT"
            else -> "DOG"
        }
    }

    private fun mapGender(gender: String): String {
        return when (gender) {
            "남" -> "MALE"
            "여" -> "FEMALE"
            else -> "FEMALE"
        }
    }

    private fun mapNeuteredStatus(neutered: String): String {
        return when (neutered) {
            "완료" -> "NEUTERED"
            "미완료" -> "NOT_NEUTERED"
            else -> "NOT_NEUTERED"
        }
    }

    private fun mapDogBreed(breed: String): String {
        return when (breed) {
            "골든리트리버" -> "GOLDEN_RETRIEVER"
            "말티즈" -> "MALTESE"
            "푸들" -> "POODLE"
            "포메라니안" -> "POMERANIAN"
            "시츄" -> "SHIH_TZU"
            "비숑프리제" -> "BICHON_FRISE"
            "치와와" -> "CHIHUAHUA"
            "닥스훈트" -> "DACHSHUND"
            "웰시코기" -> "WELSH_CORGI"
            "시바견" -> "SHIBA_INU"
            "진돗개" -> "JINDO"
            "프렌치불독" -> "FRENCH_BULLDOG"
            else -> "GOLDEN_RETRIEVER"
        }
    }

    private fun mapCatBreed(breed: String): String {
        return when (breed) {
            "코리안숏헤어" -> "KOREAN_SHORTHAIR"
            "러시안블루" -> "RUSSIAN_BLUE"
            "페르시안" -> "PERSIAN"
            "샴" -> "SIAMESE"
            "스코티시폴드" -> "SCOTTISH_FOLD"
            "먼치킨" -> "MUNCHKIN"
            "브리티시숏헤어" -> "BRITISH_SHORTHAIR"
            "아메리칸숏헤어" -> "AMERICAN_SHORTHAIR"
            "뱅갈" -> "BENGAL"
            "메인쿤" -> "MAINE_COON"
            "랙돌" -> "RAGDOLL"
            "터키시앙고라" -> "TURKISH_ANGORA"
            else -> "KOREAN_SHORTHAIR"
        }
    }

    private fun formatMealTime(time: String): String? {
        val trimmed = time.trim()
            .replace("：", ":")
            .replace(".", ":")

        if (trimmed.isBlank()) return null

        val parts = trimmed.split(":")
        if (parts.size != 2) return null

        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null

        if (hour !in 0..23 || minute !in 0..59) return null

        return "%02d:%02d".format(hour, minute)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        const val MODE_REGISTER = "register"
        const val MODE_EDIT = "edit"

        fun newInstance(mode: String): PetRegisterFragment {
            return PetRegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}
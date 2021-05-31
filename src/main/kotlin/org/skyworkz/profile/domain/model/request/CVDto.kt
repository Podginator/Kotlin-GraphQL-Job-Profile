package org.skyworkz.profile.domain.model.request

data class CVDto(
    val name: String,
    val contact: ContactDto,
    val info: InfoDTO,
    val location: LocationDto,
    val employment: EmploymentDto,
    val education: EducationDto,
    val skills: SkillsDto,
    val recognition: List<RecognitionDto>,
    val interest: List<InterestDto>,
    val references: List<ReferenceDto>,
    val languages: List<LanguageDto>
)

data class InfoDTO(
    val label: String,
    val brief: String
)

data class ContactDto(
    val email: String,
    val phone: String
)

data class LocationDto(
    val city: String,
    val country: String,
    val code: String,
    val address: String
)

data class EmploymentDto(
    val history: List<EmploymentDetailsDto>
)

data class EmploymentDetailsDto(
    val position: String,
    val employer: String,
    val summary: String,
    val start: String,
    val end: String,
    val keywords: List<String>,
    val highlights: List<String>
)

data class EducationDto(
    val level: String,
    val degree: String,
    val history: List<EducationHistoryDto>
)

data class EducationHistoryDto(
    val institution: String,
    val start: String,
    val end: String,
    val summary: String,
    val area: String,
    val studyType: String,
    val location: String,
    val title: String
)

data class SkillsDto(
    val sets: List<SkillsetDto>
)

data class SkillsetDto(
    val name: String,
    val level: String,
    val skills: List<String>
)

data class RecognitionDto(
    val title: String,
    val date: String,
    val from: String,
    val summary: String
)

data class InterestDto(
    val name: String,
    val highlights: List<String>
)

data class ReferenceDto(
    val name: String,
    val flavor: String,
    val role: String,
    val type: String
)

data class LanguageDto(
    val language: String,
    val fluency: String
)
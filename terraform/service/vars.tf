variable "docker_tag" {
  description = "The Docker Tag of the image to use"
}

variable "encrypted_jwt_secret" {
  description = "The JWT Secret"
}

variable "encrypted_sql_password" {
  description = "SQL Password"
}

variable "encrypted_api_key" {
  description = "The API Key"
}

variable "encrypted_firebase_creds" {
  description = "The firebase credentials"
}
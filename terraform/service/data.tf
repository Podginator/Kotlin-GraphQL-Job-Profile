data "aws_kms_secrets" "encrypted_vars" {
  secret {
    name = "jwt_key"
    payload = var.encrypted_jwt_secret
  }

  secret {
    name = "sql_password"
    payload = var.encrypted_sql_password
  }

  secret {
    name = "api_key"
    payload = var.encrypted_api_key
  }

  secret {
    name = "firebase"
    payload = var.encrypted_firebase_creds
  }
}

data "aws_caller_identity" "current" {}


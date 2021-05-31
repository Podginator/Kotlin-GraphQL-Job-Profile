resource "aws_ssm_parameter" "jwt_key" {
  name       = "/${local.ssm_name}/JWT_KEY"
  description = "JWT Key"
  type        = "SecureString"
  value       = data.aws_kms_secrets.encrypted_vars.plaintext["jwt_key"]
}

resource "aws_ssm_parameter" "sql_password" {
  name       = "/${local.ssm_name}/SQL_PASSWORD"
  description = "SQL Key"
  type        = "SecureString"
  value       = data.aws_kms_secrets.encrypted_vars.plaintext["sql_password"]
}

resource "aws_ssm_parameter" "sql_url" {
  name       = "/${local.ssm_name}/SQL_URL"
  description = "SQL Key"
  type        = "String"
  value       = aws_rds_cluster.skyworkz_rds.endpoint
}

resource "aws_ssm_parameter" "firebase" {
  name = "/${local.ssm_name}/FIREBASE_CREDS"
  description = "The firebase credentials file"
  type = "SecureString"
  value = data.aws_kms_secrets.encrypted_vars.plaintext["firebase"]
}

resource "aws_ssm_parameter" "api_key" {
  name = "/${local.ssm_name}/API_KEY"
  description = "The api key"
  type = "SecureString"
  value = data.aws_kms_secrets.encrypted_vars.plaintext["api_key"]
}
resource "aws_dynamodb_table" "notification-dynamodb-table" {
  name           = "${local.service_name}-notifications-storage"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5
  hash_key       = "id"
  range_key      = "userAgent"

  attribute {
    name = "id"
    type = "N"
  }

  attribute {
    name = "userAgent"
    type = "S"
  }

}
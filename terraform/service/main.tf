provider "aws" {
  region = "eu-central-1"
}

terraform {
  required_version = "~> 0.12.0"
  backend "s3" {
    bucket = "workz-tfstate"
    key    = "global/skyworkz-service/terraform.tfstate"
    region = "eu-central-1"

    dynamodb_table = "terraform-state-lock-dynamo"
    encrypt        = true
  }
}


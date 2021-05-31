locals {
  service_name = "skyworkz-profile"
  ssm_name = "skyworkz"
  account_id = data.aws_caller_identity.current.account_id
  aws_region = "eu-central-1"
  certificate_arn = "arn:aws:acm:eu-central-1:571690686990:certificate/3f7f24da-9f44-4db4-851e-048ed91f6815"

  public_subnet_ids = module.vpc.public_subnets
  private_subnet_ids = module.vpc.private_subnets
  vpc_id = module.vpc.vpc_id
}


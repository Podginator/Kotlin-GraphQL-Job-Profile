locals {
  port                 = "3306"
  master_password      = data.aws_kms_secrets.encrypted_vars.plaintext["sql_password"]
  db_subnet_group_name = join("", aws_db_subnet_group.this.*.name)
  rds_security_group_id = join("", aws_security_group.rds_sg.*.id)
  name = "aurora-${local.service_name}"
  allowed_security_groups = [ aws_security_group.instance_sg.id ]
  sql_username = "skyworkz"
}

resource "aws_db_subnet_group" "this" {
  name        = "${local.name}-subnet-group"
  description = "For Aurora cluster ${local.name}"
  subnet_ids  = local.public_subnet_ids
}

resource "aws_rds_cluster" "skyworkz_rds" {
  cluster_identifier                  = local.name
  engine                              = "aurora"
  engine_mode                         = "serverless"
  database_name                       = "skyworkz"
  master_username                     = local.sql_username
  master_password                     = local.master_password
  port                                = local.port
  db_subnet_group_name                = local.db_subnet_group_name
  vpc_security_group_ids              = compact(concat(aws_security_group.instance_sg.*.id,[ aws_security_group.rds_sg.id]))
  skip_final_snapshot = true

  scaling_configuration {
    auto_pause               = true
    max_capacity             = 256
    min_capacity             = 2
    seconds_until_auto_pause = 300
    timeout_action           = "ForceApplyCapacityChange"
  }
}

resource "aws_security_group" "rds_sg" {
  name_prefix = "${local.name}-"
  vpc_id      = local.vpc_id

  description =  "Control traffic to/from RDS Aurora ${local.name}"

}

resource "aws_security_group_rule" "default_ingress" {
  count = length(local.allowed_security_groups)

  description = "From allowed SGs"

  type                     = "ingress"
  from_port                = aws_rds_cluster.skyworkz_rds.port
  to_port                  = aws_rds_cluster.skyworkz_rds.port
  protocol                 = "tcp"
  source_security_group_id = element(local.allowed_security_groups, count.index)
  security_group_id        = local.rds_security_group_id
}

resource "aws_security_group_rule" "cidr_ingress" {
  count = length(local.allowed_security_groups)

  description = "From allowed SGs"

  type                     = "ingress"
  from_port                = aws_rds_cluster.skyworkz_rds.port
  to_port                  = aws_rds_cluster.skyworkz_rds.port
  protocol                 = "tcp"
  cidr_blocks              = ["10.0.102.0/24", "10.0.101.0/24", "10.0.1.0/24", "10.0.2.0/24"]
  security_group_id        = local.rds_security_group_id
}


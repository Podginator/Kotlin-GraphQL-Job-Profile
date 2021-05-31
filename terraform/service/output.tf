output "cluster" {
  value = aws_ecs_cluster.main.id
}

output "rds_security_group" {
    value = local.rds_security_group_id
}
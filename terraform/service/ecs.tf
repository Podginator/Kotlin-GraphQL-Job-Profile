resource "aws_ecs_cluster" "main" {
  name = local.service_name
}

resource "aws_ecs_service" "skyworkz_users" {
  name            = "${local.service_name}-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.user_service.arn
  desired_count   = 2
  iam_role        = aws_iam_role.ecs_service.arn
  depends_on      = [aws_iam_role_policy.ecs_service]

  load_balancer {
    target_group_arn = aws_alb_target_group.user_load_balancer.arn
    container_name   = "skyworkz"
    container_port   = 7000
  }
}

data "template_file" "task_definition" {
  template = file("${path.module}/task-definition.json")

  vars = {
    log_group_name      = aws_cloudwatch_log_group.app.name
    docker_tag          = var.docker_tag
    aws_account_region  = local.aws_region
    aws_account_id      = local.account_id
    dynamodb_table_name = aws_dynamodb_table.notification-dynamodb-table.name
    sql_user            = local.sql_username
  }
}

resource "aws_ecs_task_definition" "user_service" {
  family                = "${local.service_name}-service-definition"
  container_definitions = data.template_file.task_definition.rendered
}

resource "aws_appautoscaling_target" "ecs_target" {
  max_capacity       = 2
  min_capacity       = 2
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.skyworkz_users.name}"
  role_arn           = aws_iam_role.ecs_scaling_role.arn
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}


resource "aws_cloudwatch_log_group" "ecs" {
  name = "ecs-agent-sw"
}

resource "aws_cloudwatch_log_group" "app" {
  name = "${local.service_name}-logs"
}


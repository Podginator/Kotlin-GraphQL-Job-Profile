resource "aws_alb_target_group" "user_load_balancer" {
  name     = "${local.service_name}-target-group"
  port     = 7000
  protocol = "HTTP"
  vpc_id   = local.vpc_id

  health_check {
    enabled = true
    path    = "/health"
    port    = 7000
  }

  depends_on = [aws_alb.main]
}

resource "aws_alb" "main" {
  name            = "${local.service_name}-alb"
  subnets         = local.public_subnet_ids
  security_groups = [aws_security_group.lb_sg.id]
}

resource "aws_alb_listener" "user_listener" {
  load_balancer_arn = aws_alb.main.id
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = local.certificate_arn

  default_action  {
    target_group_arn = aws_alb_target_group.user_load_balancer.arn
    type             = "forward"
  }
}


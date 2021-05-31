resource "aws_autoscaling_group" "users_autoscaling_group" {
  name                 = local.service_name
  vpc_zone_identifier  = local.private_subnet_ids
  min_size             = "1"
  max_size             = "3"
  desired_capacity     = "3"
  launch_configuration = aws_launch_configuration.users_launch_config.name

  target_group_arns = [aws_alb_target_group.user_load_balancer.arn]
}

data "aws_ami" "amazon_linux_ecs" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn-ami-*-amazon-ecs-optimized"]
  }

  filter {
    name   = "owner-alias"
    values = ["amazon"]
  }
}

data "template_file" "user_data" {
  template = file("${path.module}/user-data.tpl")

  vars = {
    cluster_name = local.service_name
  }
}

resource "aws_launch_configuration" "users_launch_config" {
  key_name                    = "TomR"
  image_id                    = data.aws_ami.amazon_linux_ecs.id
  instance_type               = "t2.micro"
  iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
  user_data                   = data.template_file.user_data.rendered
  security_groups             = [aws_security_group.instance_sg.id]

  lifecycle {
    create_before_destroy = true
  }
}


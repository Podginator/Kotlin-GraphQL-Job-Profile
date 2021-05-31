resource "aws_security_group" "lb_sg" {
  name        = "user security group"
  description = "Allow Traffic to the LoadBalancer"
  vpc_id      = local.vpc_id

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"

    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
}

resource "aws_security_group" "instance_sg" {
  description = "controls direct access to application instances"
  vpc_id      = local.vpc_id
  name        = "instance_security_group"

  ingress {
    protocol  = "tcp"
    from_port = 22
    to_port   = 22

    cidr_blocks = [
      "95.174.67.212/32",
    ]
  }

  ingress {
    protocol  = -1
    from_port = 0
    to_port   = 0

    cidr_blocks = [
      "10.0.101.0/24",
      "10.0.1.0/24",
      "10.0.102.0/24",
      "10.0.2.0/24",
    ]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


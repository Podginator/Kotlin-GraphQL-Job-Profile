resource "aws_ecr_repository" "skyworkz_ecr" {
  name  = "skyworkz"
}

resource "aws_ecr_lifecycle_policy" "skyworkz_ecr_lifecycle" {
  repository = aws_ecr_repository.skyworkz_ecr.name
   policy = <<EOF
{
  "rules": [
    {
      "rulePriority": 1,
      "description": "Remove untagged images",
      "selection": {
        "tagStatus": "untagged",
        "countType": "imageCountMoreThan",
        "countNumber": 1
      },
      "action": {
        "type": "expire"
      }
    },
    {
      "rulePriority": 2,
      "description": "Rotate images when reach 10 images stored",
      "selection": {
        "tagStatus": "any",
        "countType": "imageCountMoreThan",
        "countNumber": 10
      },
      "action": {
        "type": "expire"
      }
    }
  ]
}
EOF
}

data "aws_iam_policy_document" "resource_full_access" {
  statement {
    sid = "FullAccess"
    effect = "Allow"

    principals {
      type = "AWS"

      identifiers = [ data.aws_caller_identity.current.account_id ]
    }

    actions = [
      "ecr:GetAuthorizationToken",
      "ecr:InitiateLayerUpload",
      "ecr:UploadLayerPart",
      "ecr:CompleteLayerUpload",
      "ecr:PutImage",
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:GetRepositoryPolicy",
      "ecr:DescribeRepositories",
      "ecr:ListImages",
      "ecr:DescribeImages",
      "ecr:BatchGetImage",
    ]
  }
}

resource "aws_ecr_repository_policy" "ecr_iam" {
  repository = aws_ecr_repository.skyworkz_ecr.name
  policy = data.aws_iam_policy_document.resource_full_access.json
}
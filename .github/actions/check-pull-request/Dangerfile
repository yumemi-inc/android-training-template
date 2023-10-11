# ref https://danger.systems/

# GitHub Actions のワークフローのステータス
job_status = ENV['JOB_STATUS']

# Ignore inline messages which lay outside a diff's range of PR
github.dismiss_out_of_range_messages({
  error: false,
  warning: true,
  message: true,
  markdown: true
})

# ktlint
Dir.glob("**/build/reports/ktlint/**/*.xml").each { |report|
    checkstyle_format.base_path = Dir.pwd
    checkstyle_format.report report.to_s
}

# Android Lint
Dir.glob("**/build/reports/lint-results*.xml").each { |report|
    android_lint.skip_gradle_task = true
    android_lint.report_file = report.to_s
    android_lint.filtering = false # errorレベルは変更ファイル外でも検知
    android_lint.lint(inline_mode: true)
}

# warning 数は Android Lint と ktlint のみの合計としたいのでここで変数に保存
warning_count = status_report[:warnings].count

# Local unit test
Dir.glob("**/build/test-results/**/*.xml").each { |report|
  junit.parse report
  junit.show_skipped_tests = true # スキップしたテストをワーニングとする(状況により適宜変更)
  junit.report
}

# 説明がない場合
fail 'Write at least one line in the description of PR.' if github.pr_body.length < 1

# プルリクが大きい場合
warn 'Changes have exceeded 500 lines. Divide if possible.' if git.lines_of_code > 500

# Dangerでエラーがある場合は既に何かしらコメントされているのでここで終了
return unless status_report[:errors].empty?

# ワークフローのどこかでエラーがあった場合はその旨をコメントして終了
return markdown ':heavy_exclamation_mark:Pull request check failed.' if job_status != 'success'

# 成功のコメント
comment = ':heavy_check_mark:Pull request check passed.'
if warning_count == 0
  markdown comment
else
  markdown comment + " (But **#{warning_count}** warnings reported by Android Lint and ktlint.)"
end

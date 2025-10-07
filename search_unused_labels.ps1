ECHO "Search used labels"
$used_labels = New-Object Collections.Generic.List[String]
Get-ChildItem ".\src\main" -Recurse | Foreach-Object {
    $target = get-item $_.FullName
    if (!$target.PSIsContainer)
    {
        Get-Content -Encoding utf8 -Path $_.FullName | ForEach-Object {
            if ($_.Contains('getMessage("'))
            {
                $line = $_
                while ($line.IndexOf('getMessage("') -ne -1)
                {
                    $line = $line.Substring($line.IndexOf('getMessage("') + 12)
                    $used_labels.Add($line.split('"')[0])
                }
            }
            if ($_.Contains("getMessage('"))
            {
                $line = $_
                while ($line.IndexOf("getMessage('") -ne -1)
                {
                    $line = $line.Substring($line.IndexOf("getMessage('") + 12)
                    $used_labels.Add($line.split("'")[0])
                }
            }
            if ($_.Contains('getMessage(resource, "'))
            {
                $line = $_
                while ($line.IndexOf('getMessage(resource, "') -ne -1)
                {
                    $line = $line.Substring($line.IndexOf('getMessage(resource, "') + 22)
                    $used_labels.Add($line.split('"')[0])
                }
            }
            if ($_.Contains("getMessage(resource, '"))
            {
                $line = $_
                while ($line.IndexOf("getMessage(resource, '") -ne -1)
                {
                    $line = $line.Substring($line.IndexOf("getMessage(resource, '") + 22)
                    $used_labels.Add($line.split("'")[0])
                }
            }
            if ($_.Contains('<spring:message code="'))
            {
                $line = $_
                while ($line.IndexOf('<spring:message code="') -ne -1)
                {
                    $line = $line.Substring($line.IndexOf('<spring:message code="') + 22)
                    $used_labels.Add($line.split('"')[0])
                }
            }
            if ($_.Contains("<spring:message code='"))
            {
                $line = $_
                while ($line.IndexOf("<spring:message code='") -ne -1)
                {
                    $line = $line.Substring($line.IndexOf("<spring:message code='") + 22)
                    $used_labels.Add($line.split("'")[0])
                }
            }
        }
    }
}

$used_labels = $used_labels | Sort-Object | Get-Unique

# Labels always threated as used (check before running)
$force_used = New-Object Collections.Generic.List[String]
# Activity object
$force_used.Add("label.Survey")
$force_used.Add("label.DraftSurvey")
$force_used.Add("label.SurveyAndDraft")
$force_used.Add("label.Activities")
$force_used.Add("label.Results")
$force_used.Add("label.Contribution")
$force_used.Add("label.TestContribution")
$force_used.Add("label.GuestList")
$force_used.Add("label.Privileges")
$force_used.Add("label.Messages")
$force_used.Add("label.Comment")

# Activity property
$force_used.Add("label.n/a")
$force_used.Add("label.State")
$force_used.Add("label.PendingChanges")
$force_used.Add("label.Alias")
$force_used.Add("label.EndNotificationState")
$force_used.Add("label.EndNotificationValue")
$force_used.Add("label.EndNotificationReach")
$force_used.Add("label.ContactCreation")
$force_used.Add("label.Security")
$force_used.Add("label.Password")
$force_used.Add("label.Anonymity")
$force_used.Add("label.Privacy")
$force_used.Add("label.Captcha")
$force_used.Add("label.EditContribution")
$force_used.Add("label.MultiPaging")
$force_used.Add("label.PageWiseValidation")
$force_used.Add("label.WCAGCompliance")
$force_used.Add("label.Owner")
$force_used.Add("label.ProgressBar")
$force_used.Add("label.MotivationPopUp")
$force_used.Add("label.Properties")
$force_used.Add("label.UsefulLink")
$force_used.Add("label.BackgroundDocument")
$force_used.Add("label.Title")
$force_used.Add("label.PivotLanguage")
$force_used.Add("label.Contact")
$force_used.Add("label.Autopublish")
$force_used.Add("label.StartDate")
$force_used.Add("label.EndDate")
$force_used.Add("label.Logo")
$force_used.Add("label.Skin")
$force_used.Add("label.AutoNumberingSections")
$force_used.Add("label.AutoNumberingQuestions")
$force_used.Add("label.ElementOrder")
$force_used.Add("label.SurveyElement")
$force_used.Add("label.Translation")
$force_used.Add("label.ConfirmationPage")
$force_used.Add("label.EscapePage")
$force_used.Add("label.PublishIndividual")
$force_used.Add("label.PublishCharts")
$force_used.Add("label.PublishStatistics")
$force_used.Add("label.PublicSearch")
$force_used.Add("label.PublishQuestionSelection")
$force_used.Add("label.PublishAnswerSelection")
$force_used.Add("label.ExportStatistics")
$force_used.Add("label.ExportContent")
$force_used.Add("label.ExportCharts")
$force_used.Add("label.ExportActivities")
$force_used.Add("label.PublishUploadedElements")
$force_used.Add("label.ExportUploadedElements")
$force_used.Add("label.DeleteColumn")
$force_used.Add("label.Export")
$force_used.Add("label.Token/Contacts/Department/VoterFile")
$force_used.Add("label.Invitations")
$force_used.Add("label.EndNotificationMessage")
$force_used.Add("label.Quorum")
$force_used.Add("label.EligibleLists")
$force_used.Add("label.MaximumPreferentialVotes")
$force_used.Add("label.NumberOfSeatsToAllocate")
$force_used.Add("label.EnableResultsTestPage")

# Activity Event
$force_used.Add("label.Added")
$force_used.Add("label.Applied")
$force_used.Add("label.Modified")
$force_used.Add("label.Discarded")
$force_used.Add("label.Deleted")
$force_used.Add("label.Saved")
$force_used.Add("label.Removed")
$force_used.Add("label.Enabled")
$force_used.Add("label.Disabled")
$force_used.Add("label.Requested")
$force_used.Add("label.Returned")
$force_used.Add("label.Started")
$force_used.Add("label.Created")
$force_used.Add("label.Paused")
$force_used.Add("label.Sent")
$force_used.Add("label.Opened")
$force_used.Add("label.Submitted")
$force_used.Add("label.Accepted")
$force_used.Add("label.Rejected")
$force_used.Add("label.Expired")

# Evote Template Title
$force_used.Add("label.Brussels")
$force_used.Add("label.IspraSeville")
$force_used.Add("label.Luxembourg")
$force_used.Add("label.OutsideCommunity")
$force_used.Add("label.Standard")

ECHO "List unused labels"
$unused_labels = New-Object Collections.Generic.List[String]
Get-Content -Encoding utf8 -Path ".\src\main\webapp\WEB-INF\classes\messages_en.properties" | ForEach-Object {
    if ($_.trim() -ne "")
    {
        $split = $_.split("=")
        $last_key = $split[0].Trim()
        if (!$last_key.StartsWith("logging.") -And !$last_key.StartsWith("label.lang.") -And !$last_key.StartsWith("label.ECF.") -And !$last_key.StartsWith("domain.") -And !$last_key.StartsWith("label.dgnew."))
        {
            if (!$used_labels.Contains($last_key) -And !$force_used.Contains($last_key))
            {
                $unused_labels.Add($last_key)
                ECHO $last_key
            }
        }
    }
}

ECHO "Delete unused labels"
PAUSE
$data = New-Object Collections.Generic.List[String]
Get-Content -Encoding utf8 -Path ".\src\main\webapp\WEB-INF\classes\messages_en.properties" | ForEach-Object {
    if ($_.trim() -ne "")
    {
        $split = $_.split("=")
        $last_key = $split[0].Trim()
        if (!$unused_labels.Contains($last_key))
        {
            $data.Add($_)
        }
    }
}

$data | Set-Content -Encoding utf8NoBOM -Path ".\src\main\webapp\WEB-INF\classes\messages_en.properties"
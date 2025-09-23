ECHO "Sync label files"

$referenceData = New-Object Collections.Generic.List[String]
Get-Content -Encoding utf8 -Path ".\src\main\webapp\WEB-INF\classes\messages_en.properties" | ForEach-Object {
    if ($_.trim() -ne "")
    {
        $split = $_.split("=")
        $last_key = $split[0].Trim()
        $referenceData.Add($last_key)
    }
}

Get-ChildItem ".\src\main\webapp\WEB-INF\classes" -Filter messages_* | Foreach-Object {
    if ($_.Name -ne "messages_en.properties")
    {
        ECHO $_.Name
        $data = New-Object Collections.Generic.List[String]
        Get-Content -Encoding utf8 -Path $_.FullName | ForEach-Object {
            if ($_.trim() -ne "")
            {
                $split = $_.split("=")
                $last_key = $split[0].Trim()
                if ( $referenceData.Contains($last_key))
                {
                    $data.Add($_)
                }
            }
        }
        $data | Set-Content -Encoding utf8NoBOM -Path $_.FullName
    }
}
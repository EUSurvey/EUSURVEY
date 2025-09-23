ECHO "Clean labels"
Get-ChildItem ".\src\main\webapp\WEB-INF\classes" -Filter messages_* | Foreach-Object {
    ECHO $_.Name
    $data = new-object System.Collections.Hashtable
    $last_key = ""
    $include_next_line = $false

    Get-Content -Encoding utf8 -Path $_.FullName | ForEach-Object {
        if ($_.trim() -ne "")
        {
            if ($include_next_line)
            {
                if ($_ -match '\\$')
                {
                    $include_next_line = $true
                    $_ = $_.TrimEnd('\')
                }
                else
                {
                    $include_next_line = $false
                }
                $data[$last_key] = $data[$last_key] + $_.Trim()
            }
            else
            {
                if ($_ -match '\\$')
                {
                    $include_next_line = $true
                    $_ = $_.TrimEnd('\')
                }
                $split = $_.split("=")
                $last_key = $split[0].Trim()
                $value = ($split | Select-Object -Skip 1) -join '='
                $data[$last_key] = $value.Trim()
            }
        }
    }
    $data.GetEnumerator() | sort -Property name | ForEach-Object { "{0} = {1}" -f $_.Name, $_.Value } | Set-Content -Encoding utf8NoBOM -Path $_.FullName
}



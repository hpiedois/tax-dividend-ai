<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html class="${properties.kcHtmlClass!}"<#if realm.internationalizationEnabled> lang="${(locale.currentLanguageTag!'en')}"</#if>>

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <#if properties.meta?has_content>
        <#list properties.meta?split(" ") as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title>${msg("loginTitle",(realm.displayName!''))}</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(" ") as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(" ") as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(" ") as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>

<body class="font-sans antialiased h-screen w-screen flex items-center justify-center relative overflow-hidden">
    <!-- Dark mode detection script -->
    <script>
        if (localStorage.theme === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
            document.documentElement.classList.add('dark')
        } else {
            document.documentElement.classList.remove('dark')
        }
    </script>

    <!-- Improved Background Gradient -->
    <div class="absolute inset-0 -z-10 bg-gradient-to-br from-sky-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-900 dark:to-indigo-950"></div>

    <!-- Subtle animated gradient overlay -->
    <div class="absolute inset-0 -z-10 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(56,189,248,0.15),rgba(255,255,255,0))] dark:bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(56,189,248,0.1),rgba(0,0,0,0))]"></div>

    <div class="w-full max-w-md p-8 space-y-6 bg-white/90 dark:bg-slate-900/90 rounded-2xl shadow-2xl border border-slate-200/50 dark:border-slate-700/50 backdrop-blur-xl">
        <!-- Logo and Title -->
        <div class="flex flex-col items-center gap-4 text-center">
            <div class="w-16 h-16 rounded-xl overflow-hidden shadow-lg">
                <img src="${url.resourcesPath}/img/logo.svg" alt="Tax Dividend AI" class="w-full h-full" />
            </div>
            <h1 class="text-2xl font-bold tracking-tight text-foreground font-heading">
                ${(realm.displayName!'Tax Dividend AI')}
            </h1>
        </div>

        <#-- Messages -->
        <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
            <div class="p-4 mb-4 text-sm rounded-lg ${properties.kcAlertClass!} <#if message.type = 'success'>bg-green-100 text-green-700 dark:bg-green-800 dark:text-green-100<#elseif message.type = 'warning'>bg-yellow-100 text-yellow-700 dark:bg-yellow-800 dark:text-yellow-100<#elseif message.type = 'error'>bg-red-100 text-red-700 dark:bg-red-800 dark:text-red-100<#else>bg-blue-100 text-blue-700 dark:bg-blue-800 dark:text-blue-100</#if>" role="alert">
                <span class="font-medium">
                    <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!}"></span></#if>
                    <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>
                    <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!}"></span></#if>
                    <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>
                </span>
                ${kcSanitize(message.summary)?no_esc}
            </div>
        </#if>

        <#nested "form">

        <#if displayInfo>
            <div id="kc-info" class="${properties.kcSignUpClass!} text-center text-sm text-muted-foreground mt-4">
                <#nested "info">
            </div>
        </#if>
    </div>
</body>
</html>
</#macro>

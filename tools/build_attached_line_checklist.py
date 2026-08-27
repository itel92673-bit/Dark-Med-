from pathlib import Path

root = Path(__file__).resolve().parents[1]
source = Path('/home/ubuntu/upload/pasted_content.txt')
output = root / 'reports' / 'ATTACHED_LINE_BY_LINE_CHECKLIST_AR.md'
lines = source.read_text().splitlines()

def classification(line_no: int) -> tuple[str, str, str]:
    if line_no <= 4:
        return 'PARTIAL', 'متطلب منهجي؛ تم تحويله إلى checklist', 'هذا الملف + خطة التنفيذ الحالية'
    if 5 <= line_no <= 18:
        return 'BLOCKED', 'بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا', 'تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md'
    if 19 <= line_no <= 29:
        return 'BLOCKED', 'لا يوجد جهاز ADB صحي متصل بعد', 'reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt'
    if 30 <= line_no <= 48:
        return 'NOT TESTED', 'لا توجد نافذة runtime post-change صالحة', 'Evidence Trace Matrix E-010/E-012'
    if line_no == 51:
        return 'PASS', 'تم توثيق أن contract وحده غير كافٍ', 'الملف المرفق + E-015'
    if 49 <= line_no <= 71:
        if line_no in (57, 58):
            return 'PARTIAL', 'تم تنفيذ static/native bridge وbuild، لا runtime proof', 'vpn_protect_contract وfull CI'
        return 'BLOCKED', 'لا يوجد جهاز أو مسار شبكة runtime صالح', 'E-011 وE-015'
    if 73 <= line_no <= 86:
        return 'NOT TESTED', 'Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة', 'DARKMED_QA_FINAL_DELIVERY_AR.md'
    if 87 <= line_no <= 103:
        return 'BLOCKED', 'لا توجد نافذة network runtime لجمع leak evidence', 'E-011'
    if 104 <= line_no <= 119:
        return 'BLOCKED', 'Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي', 'DARKMED_QA_FINAL_DELIVERY_AR.md'
    if 120 <= line_no <= 134:
        return 'PARTIAL', 'WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر', 'WebViewSecurityPolicyTest وE-011'
    if 135 <= line_no <= 154:
        if line_no in (139, 140, 141, 142, 143, 147, 148, 149, 150):
            return 'PASS', 'الفحص الساكن المحلي نفذ ضمن نطاقه', 'MobSFscan/security audit APK evidence'
        if line_no == 146:
            return 'BLOCKED', 'الشهادة Debug؛ production signing غير متوفر', 'production-required audit'
        return 'PARTIAL', 'فحص محدود أو finding يحتاج قرار/بيئة إضافية', 'MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md'
    if 155 <= line_no <= 171:
        return 'BLOCKED', 'workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث', 'github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN'
    if 172 <= line_no <= 185:
        return 'PASS', 'بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI', 'BUG_FIX_DATABASE_AR.md وProtocol 7 evidence'
    if 186 <= line_no <= 196:
        return 'BLOCKED', 'Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا', 'production-required audit'
    if 197 <= line_no <= 231:
        return 'PARTIAL', 'التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested', 'DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md'
    if 232 <= line_no <= 247:
        return 'FAIL', 'Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة', 'RELEASE_READINESS_FINAL.md والتقرير النهائي'
    return 'PASS', 'قاعدة عدم تحويل BLOCKED إلى PASS مطبقة', 'Evidence Matrix وCI fail-closed contracts'

rows = [
    '# Attached File — Line-by-Line Execution Checklist',
    '',
    '> كل سطر أدناه محفوظ كما ورد في الملف المرفق. `PASS` يصف تحقق البند المحدد فقط؛ ولا يرفع أي runtime blocker إلى نجاح.',
    '',
    '| Line | Instruction | Status | Current classification/evidence | Evidence |',
    '|---:|---|---|---|---|',
]
for number, text in enumerate(lines, 1):
    status, rationale, evidence = classification(number)
    escaped = text.replace('|', '\\|').replace('\n', ' ')
    rows.append(f'| {number} | {escaped} | `{status}` | {rationale} | {evidence} |')
rows += ['', '## Current verdict', '', 'The checklist is applied to the extent supported by the local environment. The artifact remains `QA ONLY`; full device, network, cloud, and production-signing evidence are not available, so the release decision remains `NO-GO / RELEASE BLOCKED`.']
output.write_text('\n'.join(rows) + '\n')
print(f'wrote={output}')
print(f'line_count={len(lines)}')

import defaultSettings from '@/settings'

const title = defaultSettings.title || 'Evidence Upload Portal'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}

<template>
  <el-dropdown class="export-button" trigger="click" placement="top-start" @command="handleExport">
    <el-button :size="size" plain icon="el-icon-download" :loading="loading" :disabled="disabled">
      导 出<i class="el-icon-arrow-down el-icon--right" />
    </el-button>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item v-if="enablePage" command="page" :disabled="!pageIdList.length">
        导出当前页（{{ pageIdList.length }} 条）
      </el-dropdown-item>
      <el-dropdown-item v-if="enableAll" command="all" :disabled="total === 0">
        导出全部（按当前筛选条件）
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
/**
 * 通用导出按钮：配合 Pagination 的 extra 插槽使用，提供“导出当前页 / 导出全部”。
 *
 * 约定：后端导出接口为 POST，接收当前查询条件；“导出当前页”额外传一份逗号分隔的
 * 主键串（默认参数名 recordIds），后端据此只导出当前页记录，其余情况按条件导出全量。
 */
export default {
  name: 'ExportButton',
  props: {
    // 后端导出接口，如 monitor/manualElectricity/export
    url: {
      type: String,
      required: true
    },
    // 当前查询条件，原样提交给导出接口
    params: {
      type: Object,
      default() {
        return {}
      }
    },
    // 当前页数据，用于“导出当前页”
    rows: {
      type: Array,
      default() {
        return []
      }
    },
    // 当前页数据的主键字段名
    idField: {
      type: String,
      default: 'recordId'
    },
    // “导出当前页”时主键串在接口中的参数名
    idsParam: {
      type: String,
      default: 'recordIds'
    },
    // 导出文件名前缀
    fileName: {
      type: String,
      default: '导出数据'
    },
    // 当前页码，仅用于文件名
    pageNum: {
      type: Number,
      default: 1
    },
    // 总条数，0 时禁用“导出全部”
    total: {
      type: Number,
      default: -1
    },
    size: {
      type: String,
      default: 'mini'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    enablePage: {
      type: Boolean,
      default: true
    },
    enableAll: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      loading: false
    }
  },
  computed: {
    pageIdList() {
      return (this.rows || [])
        .map(row => row[this.idField])
        .filter(id => id !== undefined && id !== null && id !== '')
    }
  },
  methods: {
    handleExport(scope) {
      if (this.loading) {
        return
      }
      const params = { ...this.params }
      let label = '全部'
      if (scope === 'page') {
        if (!this.pageIdList.length) {
          this.$modal.msgWarning('当前页没有可导出的数据')
          return
        }
        params[this.idsParam] = this.pageIdList.join(',')
        label = `第${this.pageNum}页`
      }
      this.loading = true
      const stamp = new Date().getTime()
      this.download(this.url, params, `${this.fileName}_${label}_${stamp}.xlsx`).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

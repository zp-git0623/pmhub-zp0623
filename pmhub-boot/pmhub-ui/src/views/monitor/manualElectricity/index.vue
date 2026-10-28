<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="82px">
      <el-form-item label="登记日期">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          clearable
        />
      </el-form-item>
      <el-form-item label="区域名称" prop="areaName">
        <el-input v-model="queryParams.areaName" placeholder="请输入区域名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button v-hasPermi="['monitor:manualElectricity:query']" type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['monitor:manualElectricity:add']" type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增登记</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recordList">
      <el-table-column label="登记日期" align="center" prop="recordDate" width="150" />
      <el-table-column label="区域" align="center" prop="areaName" min-width="160" />
      <el-table-column label="电能用量" align="right" prop="energyKwh" width="180">
        <template slot-scope="scope">{{ scope.row.energyKwh }} kWh</template>
      </el-table-column>
      <el-table-column label="备注" align="left" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="登记人" align="center" prop="createBy" width="120" />
      <el-table-column label="登记时间" align="center" prop="createTime" width="170" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button v-hasPermi="['monitor:manualElectricity:query']" size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
          <el-button v-hasPermi="['monitor:manualElectricity:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px" append-to-body>
      <el-form ref="recordForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="登记日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择日期" :disabled="dialogMode === 'view'" style="width: 100%" />
        </el-form-item>
        <el-form-item label="区域名称" prop="areaName">
          <el-input v-model="form.areaName" maxlength="100" show-word-limit placeholder="例如：一号车间" :disabled="dialogMode === 'view'" />
        </el-form-item>
        <el-form-item label="电能用量" prop="energyKwh">
          <el-input-number v-model="form.energyKwh" :min="0" :precision="3" :step="1" controls-position="right" :disabled="dialogMode === 'view'" style="width: 100%" />
          <span class="unit-hint">单位：kWh（千瓦时）</span>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="可填写抄表说明" :disabled="dialogMode === 'view'" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <template v-if="dialogMode !== 'view'">
          <el-button type="primary" @click="submitForm">保 存</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </template>
        <el-button v-else @click="dialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addManualElectricity,
  getManualElectricity,
  listManualElectricity,
  updateManualElectricity
} from '@/api/monitor/manualElectricity'

export default {
  name: 'ManualElectricity',
  data() {
    return {
      loading: false,
      showSearch: true,
      recordList: [],
      total: 0,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        areaName: undefined
      },
      dialogVisible: false,
      dialogMode: 'add',
      dialogTitle: '',
      form: {},
      rules: {
        recordDate: [{ required: true, message: '请选择登记日期', trigger: 'change' }],
        areaName: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
        energyKwh: [{ required: true, message: '请输入电能用量', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    emptyForm() {
      const now = new Date()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      return {
        recordDate: `${now.getFullYear()}-${month}-${day}`,
        areaName: '',
        energyKwh: undefined,
        remark: ''
      }
    },
    getList() {
      this.loading = true
      const query = {
        ...this.queryParams,
        beginDate: this.dateRange && this.dateRange[0],
        endDate: this.dateRange && this.dateRange[1]
      }
      listManualElectricity(query).then(response => {
        this.recordList = response.rows
        this.total = response.total
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.queryParams.areaName = undefined
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.dialogMode = 'add'
      this.dialogTitle = '新增手工电表登记'
      this.form = this.emptyForm()
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.recordForm.clearValidate())
    },
    handleView(row) {
      this.openRecord(row.recordId, 'view')
    },
    handleEdit(row) {
      this.openRecord(row.recordId, 'edit')
    },
    openRecord(recordId, mode) {
      getManualElectricity(recordId).then(response => {
        this.form = response.data
        this.dialogMode = mode
        this.dialogTitle = mode === 'view' ? '查看手工电表登记' : '编辑手工电表登记'
        this.dialogVisible = true
      })
    },
    submitForm() {
      this.$refs.recordForm.validate(valid => {
        if (!valid) return
        const save = this.dialogMode === 'add' ? addManualElectricity : updateManualElectricity
        save(this.form).then(() => {
          this.$modal.msgSuccess(this.dialogMode === 'add' ? '新增成功' : '修改成功')
          this.dialogVisible = false
          this.getList()
        })
      })
    }
  }
}
</script>

<style scoped>
.unit-hint {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
